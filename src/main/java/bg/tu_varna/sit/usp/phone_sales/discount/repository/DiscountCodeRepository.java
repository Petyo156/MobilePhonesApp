package bg.tu_varna.sit.usp.phone_sales.discount.repository;

import bg.tu_varna.sit.usp.phone_sales.discount.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, UUID> {
    Optional<DiscountCode> findByName(String discountCode);

    List<DiscountCode> findAllByIsActiveTrue();
    List<DiscountCode> findAllByIsActiveFalse();
}
