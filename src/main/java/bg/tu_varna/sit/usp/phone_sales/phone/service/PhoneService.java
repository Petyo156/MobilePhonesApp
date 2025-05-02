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
import java.util.*;

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
    public PhoneService(PhoneRepository phoneRepository, DimensionService dimensionService, HardwareService hardwareService, OperatingSystemService operatingSystemService, ModelService modelService, ImageRepository imageRepository, DecimalFormat decimalFormat, ImageService imageService) {
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


        log.info("Phone initialized successfully");
        return phoneRepository.save(phone);
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

        return initialzeGetPhoneResponse(brandAndModel, camera, hardware, operatingSystem, dimensions, slug, images, price, discountPrice, discountPercent, quantity, modelUrl, releaseYear);
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

    public void reducePhoneQuantityAfterPurchase(Phone phone, Integer quantity) {
        phone.setQuantity(phone.getQuantity() - quantity);
        if(phone.getQuantity() == 0) {
            phone.setIsVisible(false);
            log.info("Hidden phone from store due no quantity");
        }
        phoneRepository.save(phone);
        log.info("Reduced phone quantity");
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

    private GetPhoneResponse initialzeGetPhoneResponse(BrandAndModelResponse brandAndModel, CameraResponse camera, HardwareResponse hardware, OperatingSystemResponse operatingSystem, PhoneDimensionsResponse dimensions, String slug, List<ImageResponse> images, String price, String discountPrice, String discountPercent, Integer quantity, String modelUrl, Integer releaseYear) {
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
}
