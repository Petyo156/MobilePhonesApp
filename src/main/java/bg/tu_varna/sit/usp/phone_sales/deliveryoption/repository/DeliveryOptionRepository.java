package bg.tu_varna.sit.usp.phone_sales.deliveryoption.repository;

import bg.tu_varna.sit.usp.phone_sales.deliveryoption.model.DeliveryOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryOptionRepository extends JpaRepository<DeliveryOption, UUID> {
}
