package bg.tu_varna.sit.usp.phone_sales.phone.service;

import bg.tu_varna.sit.usp.phone_sales.brand.model.Brand;
import bg.tu_varna.sit.usp.phone_sales.brand.service.BrandService;
import bg.tu_varna.sit.usp.phone_sales.camera.service.CameraService;
import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import bg.tu_varna.sit.usp.phone_sales.dimension.service.DimensionService;
import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import bg.tu_varna.sit.usp.phone_sales.hardware.service.HardwareService;
import bg.tu_varna.sit.usp.phone_sales.model.model.Model;
import bg.tu_varna.sit.usp.phone_sales.model.service.ModelService;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.service.OperatingSystemService;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.repository.PhoneRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final DimensionService dimensionService;
    private final HardwareService hardwareService;
    private final OperatingSystemService operatingSystemService;
    private final ModelService modelService;
    private final BrandService brandService;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, DimensionService dimensionService, HardwareService hardwareService, OperatingSystemService operatingSystemService, ModelService modelService, BrandService brandService, CameraService cameraService) {
        this.phoneRepository = phoneRepository;
        this.dimensionService = dimensionService;
        this.hardwareService = hardwareService;
        this.operatingSystemService = operatingSystemService;
        this.modelService = modelService;
        this.brandService = brandService;
    }

    @Transactional
    public Phone submitPhone(SubmitPhoneRequest submitPhoneRequest) {
        SubmitPhoneDimensions dimensions = submitPhoneRequest.getDimensions();
        SubmitBrandAndModel brandAndModel = submitPhoneRequest.getBrandAndModel();
        SubmitHardware hardwareInfo = submitPhoneRequest.getHardware();
        SubmitCamera cameraInfo = submitPhoneRequest.getCamera();
        SubmitOperatingSystem operatingSystemInfo = submitPhoneRequest.getOperatingSystem();

        Dimension dimension = dimensionService.submitDimension(dimensions);

        Model model = modelService.submitBrandAndModel(brandAndModel);
        Brand brand = brandService.getBrandByBrandAndModel(brandAndModel);

        Hardware hardware = hardwareService.submitHardware(hardwareInfo, cameraInfo);

        OperatingSystem operatingSystem = operatingSystemService.submitOperatingSystem(operatingSystemInfo);

        Phone phone = initializePhone(submitPhoneRequest, hardware, operatingSystem, model, brand, dimension);

        return phoneRepository.save(phone);
    }

    private Phone initializePhone(SubmitPhoneRequest submitPhoneRequest, Hardware hardware, OperatingSystem operatingSystem, Model model, Brand brand, Dimension dimension) {
        return Phone.builder()
                .dimension(dimension)
                .hardware(hardware)
                .operatingSystem(operatingSystem)
                .model(model)
                .brand(brand)
                .price(BigDecimal.valueOf(submitPhoneRequest.getPrice()))
                .quantity(submitPhoneRequest.getQuantity())
                .releaseYear(submitPhoneRequest.getReleaseYear())
                .addedDate(LocalDateTime.now())
                .build();
    }
}
