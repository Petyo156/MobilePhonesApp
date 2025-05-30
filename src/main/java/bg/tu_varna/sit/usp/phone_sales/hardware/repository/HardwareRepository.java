package bg.tu_varna.sit.usp.phone_sales.hardware.repository;

import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HardwareRepository extends JpaRepository<Hardware, UUID> {
}
