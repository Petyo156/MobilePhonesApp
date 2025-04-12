package bg.tu_varna.sit.usp.phone_sales.inventory.repository;

import bg.tu_varna.sit.usp.phone_sales.inventory.model.Inventory;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    List<Inventory> getAllByUserAndInInventoryFalse(User user);

    List<Inventory> getAllByUserAndInInventoryTrueOrderByDateTimeDesc(User user);

    Optional<Inventory> getInventoryByUserAndInInventoryFalseAndId(User user, UUID id);
}
