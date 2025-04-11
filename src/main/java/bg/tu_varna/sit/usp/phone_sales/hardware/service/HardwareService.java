package bg.tu_varna.sit.usp.phone_sales.hardware.service;

import bg.tu_varna.sit.usp.phone_sales.camera.model.Camera;
import bg.tu_varna.sit.usp.phone_sales.camera.service.CameraService;
import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import bg.tu_varna.sit.usp.phone_sales.hardware.repository.HardwareRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitCamera;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitHardware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HardwareService {
    private final HardwareRepository hardwareRepository;
    private final CameraService cameraService;

    public HardwareService(HardwareRepository hardwareRepository, CameraService cameraService) {
        this.hardwareRepository = hardwareRepository;
        this.cameraService = cameraService;
    }

    public Hardware submitHardware(SubmitHardware hardwareInfo, SubmitCamera cameraInfo) {
        Camera camera = cameraService.submitCamera(cameraInfo);
        Hardware hardware = initializeHardware(hardwareInfo, camera);

        log.info("Submitting new hardware");
        return hardwareRepository.save(hardware);
    }

    private Hardware initializeHardware(SubmitHardware hardwareInfo, Camera camera) {
        return Hardware.builder()
                .batteryCapacity(hardwareInfo.getBatteryCapacity())
                .coreCount(hardwareInfo.getCoreCount())
                .ram(hardwareInfo.getRam())
                .refreshRate(hardwareInfo.getRefreshRate())
                .resolution(hardwareInfo.getResolution())
                .screenSize(hardwareInfo.getScreenSize())
                .simType(hardwareInfo.getSimType())
                .storage(hardwareInfo.getStorage())
                .camera(camera)
                .build();
    }
}
