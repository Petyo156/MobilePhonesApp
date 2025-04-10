package bg.tu_varna.sit.usp.phone_sales.camera.service;

import bg.tu_varna.sit.usp.phone_sales.camera.model.Camera;
import bg.tu_varna.sit.usp.phone_sales.camera.repository.CameraRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.SubmitCamera;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CameraService {
    private final CameraRepository cameraRepository;

    @Autowired
    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    public Camera submitCamera(SubmitCamera cameraInfo) {
        Camera camera = initializeCamera(cameraInfo);

        log.info("Submitting new camera");
        return cameraRepository.save(camera);
    }

    private Camera initializeCamera(SubmitCamera cameraInfo) {
        return Camera.builder()
                .autofocus(cameraInfo.getAutoFocus())
                .count(cameraInfo.getCount())
                .framerate(cameraInfo.getFramerate())
                .resolution(cameraInfo.getVideoResolution())
                .type(cameraInfo.getType())
                .videoResolution(cameraInfo.getVideoResolution())
                .build();
    }
}
