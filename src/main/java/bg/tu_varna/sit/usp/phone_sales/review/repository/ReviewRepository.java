package bg.tu_varna.sit.usp.phone_sales.review.repository;

import bg.tu_varna.sit.usp.phone_sales.review.model.Review;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Optional<Review> getReviewBySaleItem_Phone_SlugAndSaleItem_Sale_User(String slug, User user);

    List<Review> getReviewsBySaleItem_Phone_Slug(String slug);
}
