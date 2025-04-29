package bg.tu_varna.sit.usp.phone_sales.orderitem.repository;

import bg.tu_varna.sit.usp.phone_sales.orderitem.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
}
