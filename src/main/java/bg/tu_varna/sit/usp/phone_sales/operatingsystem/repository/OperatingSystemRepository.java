package bg.tu_varna.sit.usp.phone_sales.operatingsystem.repository;

import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperatingSystemRepository extends JpaRepository<OperatingSystem, UUID> {
    Optional<OperatingSystem> findByTypeAndVersion(OperatingSystemType operatingSystemType, String version);
}
