package bg.tu_varna.sit.usp.phone_sales.dimension.repository;

import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DimensionRepository extends JpaRepository<Dimension, UUID> {
}
