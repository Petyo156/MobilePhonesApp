package bg.tu_varna.sit.usp.phone_sales.review.service;

import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.exception.UserHasAlreadyLeftAReviewException;
import bg.tu_varna.sit.usp.phone_sales.orderitem.model.SaleItem;
import bg.tu_varna.sit.usp.phone_sales.orderitem.service.SaleItemService;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.repository.PhoneRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.review.model.Review;
import bg.tu_varna.sit.usp.phone_sales.review.repository.ReviewRepository;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ReviewRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ReviewResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PhoneService phoneService;
    private final SaleItemService saleItemService;
    private final PhoneRepository phoneRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PhoneService phoneService, SaleItemService saleItemService, PhoneRepository phoneRepository) {
        this.reviewRepository = reviewRepository;
        this.phoneService = phoneService;
        this.saleItemService = saleItemService;
        this.phoneRepository = phoneRepository;
    }

    @Transactional
    public void postReview(ReviewRequest reviewRequest, User user, String slug) {
        if (userHasAlreadyLeftAReview(slug, user)) {
            throw new UserHasAlreadyLeftAReviewException(ExceptionMessages.USER_HAS_ALREADY_LEFT_A_REVIEW);
        }
        SaleItem saleItem = saleItemService.getSaleItemReviewForUser(user, slug);
        Review review = initializeReview(reviewRequest, saleItem, user);

        reviewRepository.save(review);

        phoneService.setRatingValueForSimilarPhones(reviewRequest.getRating().getValue(), slug);

        saleItemService.setSaleItemReview(saleItem, review);
        log.info("Review published successfully");
    }

    public boolean userHasAlreadyLeftAReview(String slug, User user) {
        Optional<Review> reviewOptional = reviewRepository.getReviewBySaleItem_Phone_SlugAndSaleItem_Sale_User(slug, user);
        if (reviewOptional.isEmpty()) {
            log.info("User has not left a review");
            return false;
        }
        log.info("User already left a review");
        return true;
    }

    public List<ReviewResponse> getAllReviewsForProduct(String slug) {
        Phone currentPhone = phoneService.getVisiblePhoneBySlug(slug);
        String modelName = currentPhone.getPhoneModel().getName();
        String brandName = currentPhone.getPhoneModel().getBrand().getName();
        Integer releaseYear = currentPhone.getReleaseYear();

        List<ReviewResponse> responses = reviewRepository.findAll().stream()
                .filter(review -> {
                    Phone phone = review.getSaleItem().getPhone();
                    return phone.getPhoneModel().getName().equals(modelName) &&
                           phone.getPhoneModel().getBrand().getName().equals(brandName) &&
                           phone.getReleaseYear().equals(releaseYear);
                })
                .map(this::initializeReviewResponse)
                .distinct()
                .collect(Collectors.toList());

        log.info("Fetched all reviews for product and its variants");
        return responses;
    }

    private ReviewResponse initializeReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .name(review.getName())
                .comment(review.getComment())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private Review initializeReview(ReviewRequest reviewRequest, SaleItem saleItem, User user) {
        return Review.builder()
                .name(reviewRequest.getName())
                .comment(reviewRequest.getComment())
                .rating(reviewRequest.getRating())
                .createdAt(LocalDateTime.now())
                .saleItem(saleItem)
                .user(user)
                .build();
    }

    @Transactional
    public void deleteReview(UUID reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            SaleItem saleItem = review.getSaleItem();
            if (saleItem != null) {
                String phoneSlug = saleItem.getPhone().getSlug();
                saleItem.setReview(null);
                saleItemService.setSaleItemReview(saleItem, null);
                reviewRepository.delete(review);
                
                // Recalculate average rating after deletion
                Phone currentPhone = phoneService.getVisiblePhoneBySlug(phoneSlug);
                String modelName = currentPhone.getPhoneModel().getName();
                String brandName = currentPhone.getPhoneModel().getBrand().getName();
                Integer releaseYear = currentPhone.getReleaseYear();

                List<Phone> allVariants = phoneService.getAllPhones().stream()
                        .map(response -> phoneService.getPhoneBySlug(response.getSlug()))
                        .filter(phone -> phone.getPhoneModel().getName().equals(modelName) &&
                                phone.getPhoneModel().getBrand().getName().equals(brandName) &&
                                phone.getReleaseYear().equals(releaseYear))
                        .collect(Collectors.toList());

                if (!allVariants.isEmpty()) {
                    List<Review> remainingReviews = new ArrayList<>();
                    for (Phone phone : allVariants) {
                        remainingReviews.addAll(phone.getSaleItems().stream()
                                .map(SaleItem::getReview)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                    }

                    BigDecimal total = BigDecimal.ZERO;
                    int count = 0;
                    for (Review remainingReview : remainingReviews) {
                        if (remainingReview.getRating() != null) {
                            total = total.add(remainingReview.getRating().getValue());
                            count++;
                        }
                    }

                    BigDecimal average = count > 0 ? 
                            total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : 
                            BigDecimal.ZERO;
                    
                    // Round to nearest 0.5
                    BigDecimal roundedAverage = average.multiply(BigDecimal.valueOf(2))
                            .setScale(0, RoundingMode.HALF_UP)
                            .divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP);

                    for (Phone phone : allVariants) {
                        phone.setRating(roundedAverage);
                        phoneRepository.save(phone);
                    }
                }
                
                log.info("Review deleted successfully and ratings updated");
            }
        }
    }
}
