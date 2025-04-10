package bg.tu_varna.sit.usp.phone_sales.model.repository;

import bg.tu_varna.sit.usp.phone_sales.brand.model.Brand;
import bg.tu_varna.sit.usp.phone_sales.model.model.PhoneModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModelRepository extends JpaRepository<PhoneModel, UUID> {
    Optional<PhoneModel> findByNameAndBrand(String modelName, Brand brand);
}
