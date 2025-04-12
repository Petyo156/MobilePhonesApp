package bg.tu_varna.sit.usp.phone_sales.phone.repository;

import bg.tu_varna.sit.usp.phone_sales.phone.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImagesRepository extends JpaRepository<Image, UUID> {
}
