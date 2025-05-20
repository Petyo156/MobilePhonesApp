package bg.tu_varna.sit.usp.phone_sales.order.repository;

import bg.tu_varna.sit.usp.phone_sales.order.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findAllByOrderByOrderDateDesc();

    List<Sale> findAllByUserIdOrderByOrderDateDesc(UUID userId);

    Optional<Sale> getSaleByOrderNumber(String orderNumber);
}
