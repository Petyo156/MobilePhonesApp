package bg.tu_varna.sit.usp.phone_sales.brand.repository;

import bg.tu_varna.sit.usp.phone_sales.brand.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    Optional<Brand> findByName(String brandName);
}
