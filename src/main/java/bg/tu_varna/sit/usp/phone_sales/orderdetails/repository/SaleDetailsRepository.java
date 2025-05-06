package bg.tu_varna.sit.usp.phone_sales.orderdetails.repository;

import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.SaleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleDetailsRepository extends JpaRepository<SaleDetails, UUID> {
}
