package bg.tu_varna.sit.usp.phone_sales.cartitem.repository;

import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}
