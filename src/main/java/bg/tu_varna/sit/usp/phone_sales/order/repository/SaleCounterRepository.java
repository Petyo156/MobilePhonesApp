package bg.tu_varna.sit.usp.phone_sales.order.repository;

import bg.tu_varna.sit.usp.phone_sales.order.model.SaleCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleCounterRepository extends JpaRepository<SaleCounter, Long> {
}
