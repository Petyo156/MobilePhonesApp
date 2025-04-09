package bg.tu_varna.sit.usp.phone_sales.user.repository;

import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
