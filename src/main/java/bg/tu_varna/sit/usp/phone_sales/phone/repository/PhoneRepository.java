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

    @Query("""
    SELECT p FROM Phone p
    WHERE p.isVisible = true
      AND p.phoneModel.name = :name
      AND p.phoneModel.brand.name = :brandName
      AND p.releaseYear = :releaseYear
      AND p.hardware.ram = :ram
      AND p.hardware.storage = :storage
    """)
    List<Phone> findSimilarPhonesWithDifferentColor(@Param("name") String name,
                                                    @Param("brandName") String brandName,
                                                    @Param("releaseYear") Integer releaseYear,
                                                    @Param("ram") Integer ram,
                                                    @Param("storage") Integer storage);

    @Query("""
    SELECT p FROM Phone p
    WHERE p.isVisible = true
      AND p.phoneModel.name = :name
      AND p.phoneModel.brand.name = :brandName
      AND p.releaseYear = :releaseYear
      AND p.hardware.ram = :ram
      AND p.dimension.color = :color
    """)
    List<Phone> findSimilarPhonesWithDifferentStorage(@Param("name") String name,
                                                    @Param("brandName") String brandName,
                                                    @Param("releaseYear") Integer releaseYear,
                                                    @Param("ram") Integer ram,
                                                    @Param("color") String color);
}
