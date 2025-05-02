package bg.tu_varna.sit.usp.phone_sales.phone.service;

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
import bg.tu_varna.sit.usp.phone_sales.phone.model.Image;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.repository.ImageRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.repository.PhoneRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    private final ImageRepository imageRepository;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, DimensionService dimensionService, HardwareService hardwareService, OperatingSystemService operatingSystemService, ModelService modelService, ImageRepository imageRepository, DecimalFormat decimalFormat, ImageService imageService) {
        this.phoneRepository = phoneRepository;
        this.dimensionService = dimensionService;
        this.hardwareService = hardwareService;
        this.operatingSystemService = operatingSystemService;
        this.modelService = modelService;
        this.imageRepository = imageRepository;
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

        log.info("Phone initialized successfully");
        return phoneRepository.save(phone);
    }

    public List<GetPhoneResponse> getSearchResult(String info) {
        List<Phone> phones = phoneRepository.searchVisiblePhonesByModelOrBrand(info);
        log.info("Get search result - {}", info);

        return initializeGetPhoneListResponse(phones);
    }

    public List<GetPhoneResponse> getMostRecentPhones() {
        List<Phone> phones = phoneRepository.findTop4ByIsVisibleTrueOrderByCreatedAtDesc();

        return initializeGetPhoneListResponse(phones);
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
                .build();
    }

    public BigDecimal calculateDiscountPrice(Phone phone) {
        BigDecimal price = phone.getPrice();
        BigDecimal discountPercent = phone.getDiscountPercent().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal discountedAmount = price.multiply(discountPercent);
        return price.subtract(discountedAmount);
    }

    public List<DifferentColorPhoneResponse> getPhonesWithDifferentColor(String slug) {
        Phone phone = getVisiblePhoneBySlug(slug);
        PhoneModel model = phone.getPhoneModel();
        Hardware hardware = phone.getHardware();

        log.info("Fetching phones with different colors");
        List<Phone> similarPhonesWithDifferentColor = phoneRepository.findSimilarPhonesWithDifferentColor
                (model.getName(), model.getBrand().getName(), phone.getReleaseYear(), hardware.getRam(), hardware.getStorage());

        List<DifferentColorPhoneResponse> responses = new ArrayList<>();
        for(Phone similarPhone : similarPhonesWithDifferentColor) {
            DifferentColorPhoneResponse response = initializeDifferentColorPhoneResponse(similarPhone);
            responses.add(response);
        }
        return responses;
    }

    public List<DifferentStoragePhoneResponse> getPhonesWithDifferentStorage(String slug) {
        Phone phone = getVisiblePhoneBySlug(slug);
        PhoneModel model = phone.getPhoneModel();
        Hardware hardware = phone.getHardware();
        Dimension dimension = phone.getDimension();

        log.info("Fetching phones with different storage");
        List<Phone> similarPhonesWithDifferentStorage = phoneRepository.findSimilarPhonesWithDifferentStorage
                (model.getName(), model.getBrand().getName(), phone.getReleaseYear(), hardware.getRam(), dimension.getColor());

        List<DifferentStoragePhoneResponse> responses = new ArrayList<>();
        for(Phone similarPhone : similarPhonesWithDifferentStorage) {
            DifferentStoragePhoneResponse response = initializeDifferentStoragePhoneResponse(similarPhone);
            responses.add(response);
        }
        return responses;
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
                .build();

        String slug = generateSlug(builtPhone);
        if (phoneRepository.getPhoneBySlug(slug).isPresent()) {
            throw new DomainException(ExceptionMessages.PHONE_WITH_THIS_SLUG_ALREADY_EXISTS);
        }

        return builtPhone.toBuilder()
                .slug(slug)
                .build();
    }

    public List<GetPhoneResponse> getAllPhones() {
        List<Phone> phones = phoneRepository.findAll();
        log.info("Get all phones for admin list");
        return initializeGetPhoneListResponse(phones);
    }

    @Transactional
    public Phone updatePhone(String slug, SubmitPhoneRequest submitPhoneRequest) {
        Phone existingPhone = getPhoneBySlug(slug);
        
        existingPhone.setPrice(BigDecimal.valueOf(submitPhoneRequest.getPrice()));
        existingPhone.setQuantity(submitPhoneRequest.getQuantity());
        existingPhone.setReleaseYear(submitPhoneRequest.getReleaseYear());
        existingPhone.setModelUrl(submitPhoneRequest.getModelUrl());
        
        SubmitPhoneDimensions dimensions = submitPhoneRequest.getDimensions();
        Dimension dimension = existingPhone.getDimension();
        dimension.setColor(dimensions.getColor());
        dimension.setIsWaterResistant(dimensions.getWaterResistance());
        dimension.setHeight(dimensions.getHeight());
        dimension.setWeight(dimensions.getWeight());
        dimension.setThickness(dimensions.getThickness());
        dimension.setWidth(dimensions.getWidth());
        dimensionService.submitDimension(dimensions);
        
        SubmitBrandAndModel brandAndModel = submitPhoneRequest.getBrandAndModel();
        PhoneModel phoneModel = existingPhone.getPhoneModel();
        phoneModel.setName(brandAndModel.getModel());
        phoneModel.getBrand().setName(brandAndModel.getBrand());
        modelService.submitBrandAndModel(brandAndModel);
        
        SubmitHardware hardwareInfo = submitPhoneRequest.getHardware();
        SubmitCamera cameraInfo = submitPhoneRequest.getCamera();
        Hardware hardware = existingPhone.getHardware();
        hardware.setRam(hardwareInfo.getRam());
        hardware.setStorage(hardwareInfo.getStorage());
        hardware.setBatteryCapacity(hardwareInfo.getBatteryCapacity());
        hardware.setScreenSize(hardwareInfo.getScreenSize());
        hardware.setSimType(hardwareInfo.getSimType());
        hardware.setRefreshRate(hardwareInfo.getRefreshRate());
        hardware.setCoreCount(hardwareInfo.getCoreCount());
        hardware.setScreenResolution(hardwareInfo.getScreenResolution());
        hardware.getCamera().setResolution(cameraInfo.getResolution());
        hardware.getCamera().setCount(cameraInfo.getCount());
        hardware.getCamera().setVideoResolution(cameraInfo.getVideoResolution());
        hardwareService.submitHardware(hardwareInfo, cameraInfo);
        
        SubmitOperatingSystem operatingSystemInfo = submitPhoneRequest.getOperatingSystem();
        OperatingSystem operatingSystem = existingPhone.getOperatingSystem();
        operatingSystem.setType(operatingSystemInfo.getOperatingSystemType());
        operatingSystem.setVersion(operatingSystemInfo.getVersion());
        operatingSystemService.submitOperatingSystem(operatingSystemInfo);
        
        String newSlug = generateSlug(existingPhone);
        if (!newSlug.equals(existingPhone.getSlug())) {
            Optional<Phone> phoneWithNewSlug = phoneRepository.getPhoneBySlug(newSlug);
            if (phoneWithNewSlug.isPresent() && !phoneWithNewSlug.get().getId().equals(existingPhone.getId())) {
                throw new DomainException(ExceptionMessages.PHONE_WITH_THIS_SLUG_ALREADY_EXISTS);
            }
            existingPhone.setSlug(newSlug);
        }
        
        log.info("Phone updated successfully");
        return phoneRepository.save(existingPhone);
    }

    public SubmitPhoneRequest convertToSubmitPhoneRequest(GetPhoneResponse phoneResponse) {
        SubmitPhoneRequest submitPhoneRequest = new SubmitPhoneRequest();

        SubmitBrandAndModel brandAndModel = new SubmitBrandAndModel();
        brandAndModel.setBrand(phoneResponse.getBrandAndModelResponse().getBrand());
        brandAndModel.setModel(phoneResponse.getBrandAndModelResponse().getModel());
        submitPhoneRequest.setBrandAndModel(brandAndModel);

        submitPhoneRequest.setPrice(Double.parseDouble(phoneResponse.getPrice().replaceAll("[^\\d.]", "")));
        submitPhoneRequest.setQuantity(phoneResponse.getQuantity());
        submitPhoneRequest.setReleaseYear(phoneResponse.getReleaseYear());

        SubmitPhoneDimensions dimensions = new SubmitPhoneDimensions();
        dimensions.setColor(phoneResponse.getDimensions().getColor());
        dimensions.setWaterResistance(phoneResponse.getDimensions().getWaterResistance());
        dimensions.setHeight(phoneResponse.getDimensions().getHeight());
        dimensions.setWeight(phoneResponse.getDimensions().getWeight());
        dimensions.setThickness(phoneResponse.getDimensions().getThickness());
        dimensions.setWidth(phoneResponse.getDimensions().getWidth());
        submitPhoneRequest.setDimensions(dimensions);

        SubmitOperatingSystem operatingSystem = new SubmitOperatingSystem();
        operatingSystem.setOperatingSystemType(phoneResponse.getOperatingSystemResponse().getOperatingSystemType());
        operatingSystem.setVersion(phoneResponse.getOperatingSystemResponse().getVersion());
        submitPhoneRequest.setOperatingSystem(operatingSystem);

        SubmitHardware hardware = new SubmitHardware();
        hardware.setRam(phoneResponse.getHardwareResponse().getRam());
        hardware.setStorage(phoneResponse.getHardwareResponse().getStorage());
        hardware.setBatteryCapacity(phoneResponse.getHardwareResponse().getBatteryCapacity());
        hardware.setScreenSize(phoneResponse.getHardwareResponse().getScreenSize());
        hardware.setSimType(phoneResponse.getHardwareResponse().getSimType());
        hardware.setRefreshRate(phoneResponse.getHardwareResponse().getRefreshRate());
        hardware.setCoreCount(phoneResponse.getHardwareResponse().getCoreCount());
        hardware.setScreenResolution(phoneResponse.getHardwareResponse().getScreenResolution());
        submitPhoneRequest.setHardware(hardware);

        SubmitCamera camera = new SubmitCamera();
        camera.setResolution(phoneResponse.getCameraResponse().getResolution());
        camera.setCount(phoneResponse.getCameraResponse().getCount());
        camera.setVideoResolution(phoneResponse.getCameraResponse().getVideoResolution());
        submitPhoneRequest.setCamera(camera);

        submitPhoneRequest.setModelUrl(phoneResponse.getModelUrl());

        return submitPhoneRequest;
    }
}
