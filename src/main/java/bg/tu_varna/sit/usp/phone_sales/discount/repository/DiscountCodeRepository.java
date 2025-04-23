package bg.tu_varna.sit.usp.phone_sales.discount.repository;

import bg.tu_varna.sit.usp.phone_sales.discount.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, UUID> {
    void deleteDiscountCodeByName(String name);
}
