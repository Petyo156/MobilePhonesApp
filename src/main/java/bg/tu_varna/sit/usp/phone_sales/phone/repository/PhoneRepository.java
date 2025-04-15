package bg.tu_varna.sit.usp.phone_sales.phone.repository;

import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, UUID> {

    @Query("SELECT p FROM Phone p WHERE p.isVisible = true AND " +
            "(LOWER(p.phoneModel.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.phoneModel.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Phone> searchVisiblePhonesByModelOrBrand(@Param("keyword") String keyword);

    List<Phone> findTop5ByIsVisibleTrueOrderByCreatedAtDesc();

    List<Phone> findAllByIsVisibleTrue();

    List<Phone> findAllByIsVisibleFalse();

    Optional<Phone> getPhoneBySlug(String slug);

    Optional<Phone> getPhoneBySlugAndIsVisibleTrue(String slug);
}
