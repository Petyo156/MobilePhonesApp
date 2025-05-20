package bg.tu_varna.sit.usp.phone_sales.phone.service;

import bg.tu_varna.sit.usp.phone_sales.camera.model.Camera;
import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import bg.tu_varna.sit.usp.phone_sales.dimension.service.DimensionService;
import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import bg.tu_varna.sit.usp.phone_sales.hardware.service.HardwareService;
import bg.tu_varna.sit.usp.phone_sales.model.model.PhoneModel;
import bg.tu_varna.sit.usp.phone_sales.model.service.ModelService;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.service.OperatingSystemService;
import bg.tu_varna.sit.usp.phone_sales.orderitem.model.SaleItem;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Image;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.repository.PhoneRepository;
import bg.tu_varna.sit.usp.phone_sales.review.model.Review;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.*;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages.PHONE_WITH_THIS_SLUG_DOESNT_EXIST;

@Service
@Slf4j
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final DimensionService dimensionService;
    private final HardwareService hardwareService;
    private final OperatingSystemService operatingSystemService;
    private final ModelService modelService;
    private final DecimalFormat decimalFormat;
    private final ImageService imageService;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, DimensionService dimensionService, HardwareService hardwareService, OperatingSystemService operatingSystemService, ModelService modelService, DecimalFormat decimalFormat, ImageService imageService) {
        this.phoneRepository = phoneRepository;
        this.dimensionService = dimensionService;
        this.hardwareService = hardwareService;
        this.operatingSystemService = operatingSystemService;
        this.modelService = modelService;
        this.decimalFormat = decimalFormat;
        this.imageService = imageService;
    }

    @Transactional
    public Phone submitPhone(SubmitPhoneRequest submitPhoneRequest, List<MultipartFile> files, int thumbnailIndex) {
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new DomainException(ExceptionMessages.SET_AT_LEAST_ONE_PHONE_PICTURE);
        }

        SubmitPhoneDimensions dimensions = submitPhoneRequest.getDimensions();
        SubmitBrandAndModel brandAndModel = submitPhoneRequest.getBrandAndModel();
        SubmitHardware hardwareInfo = submitPhoneRequest.getHardware();
        SubmitCamera cameraInfo = submitPhoneRequest.getCamera();
        SubmitOperatingSystem operatingSystemInfo = submitPhoneRequest.getOperatingSystem();

        Dimension dimension = dimensionService.submitDimension(dimensions);
        PhoneModel phoneModel = modelService.submitBrandAndModel(brandAndModel);
        Hardware hardware = hardwareService.submitHardware(hardwareInfo, cameraInfo);
        OperatingSystem operatingSystem = operatingSystemService.submitOperatingSystem(operatingSystemInfo);

        Phone phone = initializePhone(submitPhoneRequest, hardware, operatingSystem, phoneModel, dimension);

        List<Image> images = imageService.saveImages(files, thumbnailIndex, phone);
        phone.setImages(images);
        phone = phoneRepository.save(phone);

        try {
            List<Phone> differentColorPhones = getDifferentColorPhones(phone.getSlug());
            List<Phone> differentStoragePhones = getDifferentStoragePhones(phone.getSlug());

            Set<Phone> allPhoneVariants = new HashSet<>();
            allPhoneVariants.addAll(differentColorPhones);
            allPhoneVariants.addAll(differentStoragePhones);

            if (!allPhoneVariants.isEmpty()) {
                BigDecimal total = BigDecimal.ZERO;
                int count = 0;
                for (Phone existingPhone : allPhoneVariants) {
                    if (existingPhone.getRating() != null && existingPhone.getRating().compareTo(BigDecimal.ZERO) > 0) {
                        total = total.add(existingPhone.getRating());
                        count++;
                    }
                }

                if (count > 0) {
                    BigDecimal average = total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                    BigDecimal roundedAverage = roundToNearestHalf(average);
                    phone.setRating(roundedAverage);
                    phoneRepository.save(phone);
                }
            }
        } catch (Exception e) {
            log.warn("Could not link ratings for new phone: {}", e.getMessage());
        }

        log.info("Phone initialized successfully");
        return phone;
    }

    public List<GetPhoneResponse> getSearchResult(String info) {
        List<Phone> phones = phoneRepository.searchVisiblePhonesByModelOrBrand(info);
        log.info("Get search result - {}", info);

        return initializeGetPhoneListResponse(phones);
    }

    public List<GetPhoneResponse> getMostRecentPhones() {
        List<Phone> phones = phoneRepository.findTop20ByIsVisibleTrueOrderByCreatedAtDesc();
        Set<String> uniqueConfigs = new HashSet<>();
        List<Phone> result = new ArrayList<>();

        for (Phone phone : phones) {
            String key = phone.getPhoneModel().getName() + "-" +
                    phone.getPhoneModel().getBrand().getName() + "-" +
                    phone.getHardware().getRam();

            if (uniqueConfigs.add(key)) {
                result.add(phone);
            }

            if (result.size() == 4) break;
        }
        return initializeGetPhoneListResponse(result);
    }

    public List<GetPhoneResponse> getAllVisiblePhones() {
        List<Phone> phones = phoneRepository.findAllByIsVisibleTrue();
        log.info("Get all visible phones");

        return initializeGetPhoneListResponse(phones);
    }

    public List<GetPhoneResponse> getAllHiddenPhones() {
        List<Phone> phones = phoneRepository.findAllByIsVisibleFalse();
        log.info("Get all not visible phones");

        return initializeGetPhoneListResponse(phones);
    }

    public GetPhoneResponse getPhoneResponseForVisiblePhoneBySlug(String slug) {
        log.info("Initializing phone response by slug");
        return initializeGetPhoneResponse(getVisiblePhoneBySlug(slug));
    }

    public GetPhoneResponse getPhoneResponseByPhone(Phone phone) {
        log.info("Initializing phone response by phone entity");
        return initializeGetPhoneResponse(getPhoneBySlug(phone.getSlug()));
    }

    public Phone getPhoneBySlug(String slug) {
        Optional<Phone> phone = phoneRepository.getPhoneBySlug(slug);
        if (phone.isEmpty()) {
            throw new DomainException(PHONE_WITH_THIS_SLUG_DOESNT_EXIST);
        }
        return phone.get();
    }

    public Phone getVisiblePhoneBySlug(String slug) {
        Optional<Phone> phone = phoneRepository.getPhoneBySlugAndIsVisibleTrue(slug);
        if (phone.isEmpty()) {
            throw new DomainException(PHONE_WITH_THIS_SLUG_DOESNT_EXIST);
        }
        return phone.get();
    }

    public void updateVisibility(String slug) {
        Phone phone = getPhoneBySlug(slug);
        Boolean state = phone.getIsVisible();
        phone.setIsVisible(!state);

        phoneRepository.save(phone);
        log.info("Phone visibility state updated");
    }

    @Transactional
    public void bulkUpdateVisibility(List<String> slugs, boolean makeVisible) {
        for (String slug : slugs) {
            Phone phone = getPhoneBySlug(slug);
            phone.setIsVisible(makeVisible);
            phoneRepository.save(phone);
            log.info("Bulk visibility update for {} set to {}", phone.getPhoneModel().getName(), makeVisible);
        }
    }

    public void setDiscountPercentForPhone(String slug, String discountPercent) {
        Phone phone = getPhoneBySlug(slug);
        try {
            BigDecimal fullPercent = new BigDecimal(discountPercent);
            if (fullPercent.compareTo(BigDecimal.ZERO) < 0 || fullPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new DomainException(ExceptionMessages.INVALID_DISCOUNT_PERCENT);
            }
            phone.setDiscountPercent(fullPercent);
            phoneRepository.save(phone);

            log.info("Discount for {} set to {}%", phone.getPhoneModel().getName(), fullPercent);
        } catch (NumberFormatException e) {
            throw new DomainException(ExceptionMessages.INVALID_DISCOUNT_PERCENT);
        }
    }

    public void setBulkDiscountPercentForPhones(List<String> slugs, String discountPercent) {
        try {
            BigDecimal fullPercent = new BigDecimal(discountPercent);
            if (fullPercent.compareTo(BigDecimal.ZERO) < 0 || fullPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new DomainException(ExceptionMessages.INVALID_DISCOUNT_PERCENT);
            }

            for (String slug : slugs) {
                Phone phone = getPhoneBySlug(slug);
                phone.setDiscountPercent(fullPercent);
                phoneRepository.save(phone);
                log.info("Bulk discount for {} set to {}%", phone.getPhoneModel().getName(), fullPercent);
            }
        } catch (NumberFormatException e) {
            throw new DomainException(ExceptionMessages.INVALID_DISCOUNT_PERCENT);
        }
    }

    public BigDecimal calculateDiscountPrice(Phone phone) {
        BigDecimal price = phone.getPrice();
        BigDecimal discountPercent = phone.getDiscountPercent().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal discountedAmount = price.multiply(discountPercent);
        return price.subtract(discountedAmount);
    }

    public List<DifferentColorPhoneResponse> getPhonesWithDifferentColor(String slug) {
        List<Phone> similarPhonesWithDifferentColor = getDifferentColorPhones(slug);
        List<DifferentColorPhoneResponse> responses = new ArrayList<>();
        for (Phone similarPhone : similarPhonesWithDifferentColor) {
            DifferentColorPhoneResponse response = initializeDifferentColorPhoneResponse(similarPhone);
            responses.add(response);
        }
        return responses;
    }

    public List<DifferentStoragePhoneResponse> getPhonesWithDifferentStorage(String slug) {
        List<Phone> similarPhonesWithDifferentStorage = getDifferentStoragePhones(slug);
        List<DifferentStoragePhoneResponse> responses = new ArrayList<>();
        for (Phone similarPhone : similarPhonesWithDifferentStorage) {
            DifferentStoragePhoneResponse response = initializeDifferentStoragePhoneResponse(similarPhone);
            responses.add(response);
        }
        return responses;
    }

    public void reducePhoneQuantityAfterPurchase(Phone phone, Integer quantity) {
        phone.setQuantity(phone.getQuantity() - quantity);
        if (phone.getQuantity() == 0) {
            phone.setIsVisible(false);
            log.info("Hidden phone from store due no quantity");
        }
        phoneRepository.save(phone);
        log.info("Reduced phone quantity");
    }

    public List<GetPhoneResponse> getAllPhones() {
        List<Phone> phones = phoneRepository.findAll();
        log.info("Get all phones for admin list");
        return initializeGetPhoneListResponse(phones);
    }

    @Transactional
    public Phone updatePhone(String slug, SubmitPhoneRequest request) {
        Phone phone = getPhoneBySlug(slug);

        updateBasicInfo(phone, request);
        updateDimension(phone.getDimension(), request.getDimensions());
        updateBrandAndModel(phone.getPhoneModel(), request.getBrandAndModel());
        updateHardware(phone.getHardware(), request.getHardware(), request.getCamera());
        updateOperatingSystem(phone.getOperatingSystem(), request.getOperatingSystem());

        updateSlugIfChanged(phone);

        log.info("Phone updated successfully");
        return phoneRepository.save(phone);
    }

    public String getExtendedPhoneNameBySlug(String slug) {
        Phone phoneBySlug = getPhoneBySlug(slug);
        return phoneBySlug.getPhoneModel().getBrand().getName() +
                " " + phoneBySlug.getPhoneModel().getName() +
                " " + phoneBySlug.getDimension().getColor() +
                " " + phoneBySlug.getHardware().getStorage() +
                "GB";
    }

    public SubmitPhoneRequest convertToSubmitPhoneRequest(GetPhoneResponse phoneResponse) {
        return SubmitPhoneRequest.builder()
                .brandAndModel(buildBrandAndModel(phoneResponse))
                .price(parsePrice(phoneResponse.getPrice()))
                .quantity(phoneResponse.getQuantity())
                .releaseYear(phoneResponse.getReleaseYear())
                .dimensions(buildDimensions(phoneResponse))
                .operatingSystem(buildOperatingSystem(phoneResponse))
                .hardware(buildHardware(phoneResponse))
                .camera(buildCamera(phoneResponse))
                .modelUrl(phoneResponse.getModelUrl())
                .build();
    }

    public void setRatingValueForSimilarPhones(BigDecimal newRating, String slug) {
        Phone currentPhone = getVisiblePhoneBySlug(slug);
        String modelName = currentPhone.getPhoneModel().getName();
        String brandName = currentPhone.getPhoneModel().getBrand().getName();
        Integer releaseYear = currentPhone.getReleaseYear();

        List<Phone> allVariants = phoneRepository.findAll().stream()
                .filter(phone -> phone.getPhoneModel().getName().equals(modelName) &&
                        phone.getPhoneModel().getBrand().getName().equals(brandName) &&
                        phone.getReleaseYear().equals(releaseYear))
                .toList();

        if (allVariants.isEmpty()) {
            log.warn("No phone variants found for model: {} {}", brandName, modelName);
            return;
        }

        List<Review> allReviews = new ArrayList<>();
        for (Phone phone : allVariants) {
            allReviews.addAll(phone.getSaleItems().stream()
                    .map(SaleItem::getReview)
                    .filter(Objects::nonNull)
                    .toList());
        }

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Review review : allReviews) {
            if (review.getRating() != null) {
                total = total.add(review.getRating().getValue());
                count++;
            }
        }

        total = total.add(newRating);
        count++;

        BigDecimal average = total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        BigDecimal roundedAverage = roundToNearestHalf(average);

        for (Phone phone : allVariants) {
            phone.setRating(roundedAverage);
            phoneRepository.save(phone);
        }
    }

    private BigDecimal roundToNearestHalf(BigDecimal value) {
        BigDecimal multiplied = value.multiply(BigDecimal.valueOf(2));
        BigDecimal rounded = new BigDecimal(Math.round(multiplied.doubleValue()));
        return rounded.divide(BigDecimal.valueOf(2));
    }

    private List<Phone> getDifferentColorPhones(String slug) {
        Phone phone = getVisiblePhoneBySlug(slug);
        PhoneModel model = phone.getPhoneModel();
        Hardware hardware = phone.getHardware();

        log.info("Fetching phones with different colors");
        return phoneRepository.findSimilarPhonesWithDifferentColor
                (model.getName(), model.getBrand().getName(), phone.getReleaseYear(), hardware.getRam(), hardware.getStorage());
    }

    private List<Phone> getDifferentStoragePhones(String slug) {
        Phone phone = getVisiblePhoneBySlug(slug);
        PhoneModel model = phone.getPhoneModel();
        Hardware hardware = phone.getHardware();
        Dimension dimension = phone.getDimension();

        log.info("Fetching phones with different storage");
        return phoneRepository.findSimilarPhonesWithDifferentStorage
                (model.getName(), model.getBrand().getName(), phone.getReleaseYear(), hardware.getRam(), dimension.getColor());
    }

    private String generateSlug(Phone phone) {
        log.info("Generating slug for phone");
        return (phone.getPhoneModel().getBrand().getName() + "-" + phone.getPhoneModel().getName() + "-" +
                phone.getHardware().getStorage().toString() + "gb-" + phone.getHardware().getRam().toString() + "ram-" + phone.getDimension().getColor())
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    private List<GetPhoneResponse> initializeGetPhoneListResponse(List<Phone> phones) {
        List<GetPhoneResponse> responses = new ArrayList<>();
        for (Phone phone : phones) {
            GetPhoneResponse response = initializeGetPhoneResponse(phone);
            responses.add(response);
        }
        return responses;
    }

    private GetPhoneResponse initializeGetPhoneResponse(Phone phone) {
        BrandAndModelResponse brandAndModel = initializeBrandAndModelResponse(phone);
        CameraResponse camera = initializeCameraResponse(phone);
        HardwareResponse hardware = initializeHardwareResponse(phone);
        OperatingSystemResponse operatingSystem = initializeOperatingSystemResponse(phone);
        PhoneDimensionsResponse dimensions = initializeDimensionsResponse(phone);
        List<ImageResponse> images = initializePhoneImagesResponse(phone);
        String price = decimalFormat.format(phone.getPrice());
        String discountPrice = getDiscountPrice(phone);
        String discountPercent = String.format("%.0f", phone.getDiscountPercent());
        Integer quantity = phone.getQuantity();
        String modelUrl = phone.getModelUrl();
        Integer releaseYear = phone.getReleaseYear();
        String slug = phone.getSlug();
        BigDecimal rating = phone.getRating();

        return GetPhoneResponse.builder()
                .slug(slug)
                .brandAndModelResponse(brandAndModel)
                .cameraResponse(camera)
                .hardwareResponse(hardware)
                .operatingSystemResponse(operatingSystem)
                .dimensions(dimensions)
                .images(images)
                .price(price)
                .quantity(quantity)
                .discountPercent(discountPercent)
                .discountPrice(discountPrice)
                .releaseYear(releaseYear)
                .modelUrl(modelUrl)
                .createdAt(phone.getCreatedAt())
                .isVisible(phone.getIsVisible())
                .rating(rating)
                .build();
    }

    private String getDiscountPrice(Phone phone) {
        BigDecimal finalPrice = calculateDiscountPrice(phone);
        return decimalFormat.format(finalPrice.setScale(2, RoundingMode.HALF_UP));
    }

    private DifferentColorPhoneResponse initializeDifferentColorPhoneResponse(Phone similarPhone) {
        return DifferentColorPhoneResponse.builder()
                .slug(similarPhone.getSlug())
                .imageUrl(similarPhone.getImages().get(0).getImageUrl())
                .color(similarPhone.getDimension().getColor())
                .price(getDiscountPrice(similarPhone))
                .build();
    }

    private DifferentStoragePhoneResponse initializeDifferentStoragePhoneResponse(Phone similarPhone) {
        return DifferentStoragePhoneResponse.builder()
                .slug(similarPhone.getSlug())
                .imageUrl(similarPhone.getImages().get(0).getImageUrl())
                .storage(similarPhone.getHardware().getStorage().toString())
                .build();
    }

    private List<ImageResponse> initializePhoneImagesResponse(Phone phone) {
        List<ImageResponse> images = new ArrayList<>();
        for (Image image : phone.getImages()) {
            images.add(ImageResponse.builder()
                    .imageUrl(image.getImageUrl())
                    .imageIndex(image.getImageIndex())
                    .build());
        }
        images.sort(Comparator.comparing(ImageResponse::getImageIndex));
        return images;
    }

    private PhoneDimensionsResponse initializeDimensionsResponse(Phone phone) {
        return PhoneDimensionsResponse.builder()
                .color(phone.getDimension().getColor())
                .width(phone.getDimension().getWidth())
                .height(phone.getDimension().getHeight())
                .waterResistance(phone.getDimension().getIsWaterResistant())
                .thickness(phone.getDimension().getThickness())
                .weight(phone.getDimension().getWeight())
                .build();
    }

    private OperatingSystemResponse initializeOperatingSystemResponse(Phone phone) {
        return OperatingSystemResponse.builder()
                .operatingSystemType(phone.getOperatingSystem().getType())
                .version(phone.getOperatingSystem().getVersion())
                .build();
    }

    private HardwareResponse initializeHardwareResponse(Phone phone) {
        return HardwareResponse.builder()
                .batteryCapacity(phone.getHardware().getBatteryCapacity())
                .ram(phone.getHardware().getRam())
                .refreshRate(phone.getHardware().getRefreshRate())
                .screenSize(phone.getHardware().getScreenSize())
                .simType(phone.getHardware().getSimType())
                .storage(phone.getHardware().getStorage())
                .coreCount(phone.getHardware().getCoreCount())
                .screenResolution(phone.getHardware().getScreenResolution())
                .build();
    }

    private CameraResponse initializeCameraResponse(Phone phone) {
        return CameraResponse.builder()
                .videoResolution(phone.getHardware().getCamera().getVideoResolution())
                .resolution(phone.getHardware().getCamera().getResolution())
                .count(phone.getHardware().getCamera().getCount())
                .build();
    }

    private BrandAndModelResponse initializeBrandAndModelResponse(Phone phone) {
        return BrandAndModelResponse.builder()
                .brand(phone.getPhoneModel().getBrand().getName())
                .model(phone.getPhoneModel().getName())
                .build();
    }

    private Phone initializePhone(SubmitPhoneRequest submitPhoneRequest, Hardware hardware, OperatingSystem operatingSystem, PhoneModel phoneModel, Dimension dimension) {
        log.info("Initializing phone");
        Phone builtPhone = Phone.builder()
                .dimension(dimension)
                .hardware(hardware)
                .operatingSystem(operatingSystem)
                .phoneModel(phoneModel)
                .price(BigDecimal.valueOf(submitPhoneRequest.getPrice()))
                .quantity(submitPhoneRequest.getQuantity())
                .releaseYear(submitPhoneRequest.getReleaseYear())
                .createdAt(LocalDateTime.now())
                .isVisible(true)
                .discountPercent(BigDecimal.ZERO)
                .modelUrl(submitPhoneRequest.getModelUrl())
                .rating(BigDecimal.ZERO)
                .build();

        String slug = generateSlug(builtPhone);
        if (phoneRepository.getPhoneBySlug(slug).isPresent()) {
            throw new DomainException(ExceptionMessages.PHONE_WITH_THIS_SLUG_ALREADY_EXISTS);
        }

        return builtPhone.toBuilder()
                .slug(slug)
                .build();
    }

    private SubmitBrandAndModel buildBrandAndModel(GetPhoneResponse response) {
        return SubmitBrandAndModel.builder()
                .brand(response.getBrandAndModelResponse().getBrand())
                .model(response.getBrandAndModelResponse().getModel())
                .build();
    }

    private double parsePrice(String price) {
        return Double.parseDouble(price.replaceAll("[^\\d.]", ""));
    }

    private SubmitPhoneDimensions buildDimensions(GetPhoneResponse response) {
        return SubmitPhoneDimensions.builder()
                .color(response.getDimensions().getColor())
                .waterResistance(response.getDimensions().getWaterResistance())
                .height(response.getDimensions().getHeight())
                .weight(response.getDimensions().getWeight())
                .thickness(response.getDimensions().getThickness())
                .width(response.getDimensions().getWidth())
                .build();
    }

    private SubmitOperatingSystem buildOperatingSystem(GetPhoneResponse response) {
        return SubmitOperatingSystem.builder()
                .operatingSystemType(response.getOperatingSystemResponse().getOperatingSystemType())
                .version(response.getOperatingSystemResponse().getVersion())
                .build();
    }

    private SubmitHardware buildHardware(GetPhoneResponse response) {
        HardwareResponse hardware = response.getHardwareResponse();
        return SubmitHardware.builder()
                .ram(hardware.getRam())
                .storage(hardware.getStorage())
                .batteryCapacity(hardware.getBatteryCapacity())
                .screenSize(hardware.getScreenSize())
                .simType(hardware.getSimType())
                .refreshRate(hardware.getRefreshRate())
                .coreCount(hardware.getCoreCount())
                .screenResolution(hardware.getScreenResolution())
                .build();
    }

    private SubmitCamera buildCamera(GetPhoneResponse response) {
        CameraResponse camera = response.getCameraResponse();
        return SubmitCamera.builder()
                .resolution(camera.getResolution())
                .count(camera.getCount())
                .videoResolution(camera.getVideoResolution())
                .build();
    }

    private void updateBasicInfo(Phone phone, SubmitPhoneRequest request) {
        phone.setPrice(BigDecimal.valueOf(request.getPrice()));
        phone.setQuantity(request.getQuantity());
        phone.setReleaseYear(request.getReleaseYear());
        phone.setModelUrl(request.getModelUrl());
    }

    private void updateDimension(Dimension dimension, SubmitPhoneDimensions dimensions) {
        dimension.setColor(dimensions.getColor());
        dimension.setIsWaterResistant(dimensions.getWaterResistance());
        dimension.setHeight(dimensions.getHeight());
        dimension.setWeight(dimensions.getWeight());
        dimension.setThickness(dimensions.getThickness());
        dimension.setWidth(dimensions.getWidth());
        dimensionService.submitDimension(dimensions);
    }

    private void updateBrandAndModel(PhoneModel phoneModel, SubmitBrandAndModel brandAndModel) {
        phoneModel.setName(brandAndModel.getModel());
        phoneModel.getBrand().setName(brandAndModel.getBrand());
        modelService.submitBrandAndModel(brandAndModel);
    }

    private void updateHardware(Hardware hardware, SubmitHardware submitHardware, SubmitCamera submitCamera) {
        hardware.setRam(submitHardware.getRam());
        hardware.setStorage(submitHardware.getStorage());
        hardware.setBatteryCapacity(submitHardware.getBatteryCapacity());
        hardware.setScreenSize(submitHardware.getScreenSize());
        hardware.setSimType(submitHardware.getSimType());
        hardware.setRefreshRate(submitHardware.getRefreshRate());
        hardware.setCoreCount(submitHardware.getCoreCount());
        hardware.setScreenResolution(submitHardware.getScreenResolution());

        Camera camera = hardware.getCamera();
        camera.setResolution(submitCamera.getResolution());
        camera.setCount(submitCamera.getCount());
        camera.setVideoResolution(submitCamera.getVideoResolution());

        hardwareService.submitHardware(submitHardware, submitCamera);
    }

    private void updateOperatingSystem(OperatingSystem os, SubmitOperatingSystem submitOperatingSystem) {
        os.setType(submitOperatingSystem.getOperatingSystemType());
        os.setVersion(submitOperatingSystem.getVersion());
        operatingSystemService.submitOperatingSystem(submitOperatingSystem);
    }

    private void updateSlugIfChanged(Phone phone) {
        String newSlug = generateSlug(phone);
        if (!newSlug.equals(phone.getSlug())) {
            phoneRepository.getPhoneBySlug(newSlug).ifPresent(existing -> {
                if (!existing.getId().equals(phone.getId())) {
                    throw new DomainException(ExceptionMessages.PHONE_WITH_THIS_SLUG_ALREADY_EXISTS);
                }
            });
            phone.setSlug(newSlug);
        }
    }

    public GetPhoneResponse getPhoneResponseByReviewId(UUID reviewId) {
        Optional<Phone> phone = phoneRepository.findBySaleItems_Review_Id(reviewId);
        if (phone.isEmpty()) {
            throw new DomainException(PHONE_WITH_THIS_SLUG_DOESNT_EXIST);
        }
        return getPhoneResponseByPhone(phone.get());
    }

    public List<String> getUniqueVisibleBrands() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .map(phone -> phone.getPhoneModel().getBrand().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Integer> getUniqueVisibleStorages() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .map(phone -> phone.getHardware().getStorage())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Integer> getUniqueVisibleRam() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .map(phone -> phone.getHardware().getRam())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getUniqueVisibleColors() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .map(phone -> phone.getDimension().getColor())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getUniqueVisibleCameraResolutions() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .map(phone -> phone.getHardware().getCamera().getResolution().toString())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getUniqueVisibleScreenSizes() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .map(phone -> String.format("%.1f", phone.getHardware().getScreenSize()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getUniqueVisibleWaterResistanceRatings() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .filter(phone -> phone.getDimension().getIsWaterResistant())
                .map(phone -> "IP" + (phone.getDimension().getIsWaterResistant() ? "68" : "67"))  // This is a placeholder logic - in a real implementation, you'd get the actual rating
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean hasWaterResistantPhones() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .anyMatch(phone -> phone.getDimension().getIsWaterResistant());
    }

    public List<Integer> getUniqueVisibleBatteryCapacities() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
                .map(phone -> phone.getHardware().getBatteryCapacity())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<GetPhoneResponse> getFilteredPhones(List<String> brands, List<Integer> storages, 
                                                  List<Integer> ram, Double minPrice, Double maxPrice,
                                                  List<String> colors, List<String> cameraResolutions,
                                                  List<String> screenSizes, Boolean waterResistant,
                                                  List<Integer> batteryCapacities, Boolean discountedOnly) {
        List<Phone> phones = phoneRepository.findAllByIsVisibleTrue();
        
        return phones.stream()
                .filter(phone -> brands == null || brands.isEmpty() || 
                        brands.contains(phone.getPhoneModel().getBrand().getName()))
                .filter(phone -> storages == null || storages.isEmpty() || 
                        storages.contains(phone.getHardware().getStorage()))
                .filter(phone -> ram == null || ram.isEmpty() || 
                        ram.contains(phone.getHardware().getRam()))
                .filter(phone -> minPrice == null || 
                        phone.getPrice().compareTo(BigDecimal.valueOf(minPrice)) >= 0)
                .filter(phone -> maxPrice == null || 
                        phone.getPrice().compareTo(BigDecimal.valueOf(maxPrice)) <= 0)
                .filter(phone -> colors == null || colors.isEmpty() || 
                        colors.contains(phone.getDimension().getColor()))
                .filter(phone -> cameraResolutions == null || cameraResolutions.isEmpty() || 
                        cameraResolutions.contains(phone.getHardware().getCamera().getResolution().toString()))
                .filter(phone -> screenSizes == null || screenSizes.isEmpty() || 
                        screenSizes.contains(String.format("%.1f", phone.getHardware().getScreenSize())))
                .filter(phone -> waterResistant == null || 
                        (!waterResistant || phone.getDimension().getIsWaterResistant()))
                .filter(phone -> batteryCapacities == null || batteryCapacities.isEmpty() || 
                        batteryCapacities.contains(phone.getHardware().getBatteryCapacity()))
                .filter(phone -> discountedOnly == null || !discountedOnly || 
                        (phone.getDiscountPercent() != null && phone.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0))
                .map(this::initializeGetPhoneResponse)
                .collect(Collectors.toList());
    }

    public double getMaxVisiblePhonePrice() {
        return phoneRepository.findAllByIsVisibleTrue().stream()
            .map(phone -> phone.getPrice().doubleValue())
            .max(Double::compareTo)
            .orElse(3000.0);
    }
}
