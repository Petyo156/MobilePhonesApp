package bg.tu_varna.sit.usp.phone_sales.phone.repository;

import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, UUID> {

}
