package bg.tu_varna.sit.usp.phone_sales.orderitem.repository;

import bg.tu_varna.sit.usp.phone_sales.orderitem.model.SaleItem;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
    Optional<SaleItem> findFirstSaleItemByPhone_SlugAndSale_User(String slug, User user);
}
