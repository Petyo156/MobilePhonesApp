package bg.tu_varna.sit.usp.phone_sales.camera.repository;

import bg.tu_varna.sit.usp.phone_sales.camera.model.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CameraRepository extends JpaRepository<Camera, UUID> {
}
