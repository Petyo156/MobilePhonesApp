package bg.tu_varna.sit.usp.phone_sales.hardware.service;

import bg.tu_varna.sit.usp.phone_sales.camera.model.Camera;
import bg.tu_varna.sit.usp.phone_sales.camera.service.CameraService;
import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import bg.tu_varna.sit.usp.phone_sales.hardware.repository.HardwareRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.SubmitCamera;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.SubmitHardware;
import org.springframework.stereotype.Service;

@Service
public class HardwareService {
    private final HardwareRepository hardwareRepository;
    private final CameraService cameraService;

    public HardwareService(HardwareRepository hardwareRepository, CameraService cameraService) {
        this.hardwareRepository = hardwareRepository;
        this.cameraService = cameraService;
    }

    public Hardware submitHardware(SubmitHardware hardwareInfo, SubmitCamera cameraInfo, Phone phone) {
        Camera camera = cameraService.submitCamera(cameraInfo);
        Hardware hardware = initializeHardware(hardwareInfo, camera, phone);

        return hardwareRepository.save(hardware);
    }

    private Hardware initializeHardware(SubmitHardware hardwareInfo, Camera camera, Phone phone) {
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
                .phone(phone)
                .build();
    }
}
