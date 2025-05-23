package bg.tu_varna.sit.usp.phone_sales.cart.repository;

import bg.tu_varna.sit.usp.phone_sales.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
}
