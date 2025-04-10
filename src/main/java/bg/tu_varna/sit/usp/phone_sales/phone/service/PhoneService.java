package bg.tu_varna.sit.usp.phone_sales.phone.service;

import bg.tu_varna.sit.usp.phone_sales.brand.service.BrandService;
import bg.tu_varna.sit.usp.phone_sales.camera.service.CameraService;
import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import bg.tu_varna.sit.usp.phone_sales.dimension.service.DimensionService;
import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import bg.tu_varna.sit.usp.phone_sales.hardware.service.HardwareService;
import bg.tu_varna.sit.usp.phone_sales.model.model.PhoneModel;
import bg.tu_varna.sit.usp.phone_sales.model.service.ModelService;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.service.OperatingSystemService;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.repository.PhoneRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final DimensionService dimensionService;
    private final HardwareService hardwareService;
    private final OperatingSystemService operatingSystemService;
    private final ModelService modelService;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, DimensionService dimensionService, HardwareService hardwareService, OperatingSystemService operatingSystemService, ModelService modelService, BrandService brandService, CameraService cameraService) {
        this.phoneRepository = phoneRepository;
        this.dimensionService = dimensionService;
        this.hardwareService = hardwareService;
        this.operatingSystemService = operatingSystemService;
        this.modelService = modelService;
    }

    @Transactional
    public Phone submitPhone(SubmitPhoneRequest submitPhoneRequest) {
        Phone phone = Phone.builder().build();

        SubmitPhoneDimensions dimensions = submitPhoneRequest.getDimensions();
        SubmitBrandAndModel brandAndModel = submitPhoneRequest.getBrandAndModel();
        SubmitHardware hardwareInfo = submitPhoneRequest.getHardware();
        SubmitCamera cameraInfo = submitPhoneRequest.getCamera();
        SubmitOperatingSystem operatingSystemInfo = submitPhoneRequest.getOperatingSystem();

        Dimension dimension = dimensionService.submitDimension(dimensions);

        PhoneModel phoneModel = modelService.submitBrandAndModel(brandAndModel);

        Hardware hardware = hardwareService.submitHardware(hardwareInfo, cameraInfo);

        OperatingSystem operatingSystem = operatingSystemService.submitOperatingSystem(operatingSystemInfo);

        Phone initializedPhone = initializePhone(phone, submitPhoneRequest, hardware, operatingSystem, phoneModel, dimension);

        log.info("Phone initialized successfully");
        return phoneRepository.save(initializedPhone);
    }

    private Phone initializePhone(Phone phone, SubmitPhoneRequest submitPhoneRequest, Hardware hardware, OperatingSystem operatingSystem, PhoneModel phoneModel, Dimension dimension) {
        log.info("Initializing phone");
        return phone.toBuilder()
                .dimension(dimension)
                .hardware(hardware)
                .operatingSystem(operatingSystem)
                .phoneModel(phoneModel)
                .price(BigDecimal.valueOf(submitPhoneRequest.getPrice()))
                .quantity(submitPhoneRequest.getQuantity())
                .releaseYear(submitPhoneRequest.getReleaseYear())
                .addedDate(LocalDateTime.now())
                .build();
    }
}
