package bg.tu_varna.sit.usp.phone_sales.inventory.repository;

import bg.tu_varna.sit.usp.phone_sales.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

}
