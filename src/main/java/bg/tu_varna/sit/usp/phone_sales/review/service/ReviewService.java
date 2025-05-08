package bg.tu_varna.sit.usp.phone_sales.review.service;

import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.orderitem.model.SaleItem;
import bg.tu_varna.sit.usp.phone_sales.orderitem.service.SaleItemService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PhoneService phoneService;
    private final SaleItemService saleItemService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PhoneService phoneService, SaleItemService saleItemService) {
        this.reviewRepository = reviewRepository;
        this.phoneService = phoneService;
        this.saleItemService = saleItemService;
    }

    @Transactional
    public void postReview(ReviewRequest reviewRequest, User user, String slug) {
        if (userHasAlreadyLeftAReview(slug, user)) {
            throw new DomainException(ExceptionMessages.USER_HAS_ALREADY_LEFT_A_REVIEW);
        }
        SaleItem saleItem = saleItemService.getSaleItemReviewForUser(user, slug);
        Review review = initializeReview(reviewRequest, saleItem);

        reviewRepository.save(review);

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
        List<String> allVariantSlugs = new ArrayList<>();
        phoneService.getPhonesWithDifferentColor(slug)
                .forEach(phone -> allVariantSlugs.add(phone.getSlug()));
        phoneService.getPhonesWithDifferentStorage(slug)
                .forEach(phone -> allVariantSlugs.add(phone.getSlug()));

        List<ReviewResponse> responses = allVariantSlugs.stream()
                .flatMap(variantSlug -> reviewRepository
                        .getReviewsBySaleItem_Phone_Slug(variantSlug)
                        .stream()
                        .map(this::initializeReviewResponse))
                .distinct()
                .collect(Collectors.toList());

        log.info("Fetched all reviews for product");
        return responses;
    }

    private ReviewResponse initializeReviewResponse(Review review) {
        return ReviewResponse.builder()
                .name(review.getName())
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }

    private Review initializeReview(ReviewRequest reviewRequest, SaleItem saleItem) {
        return Review.builder()
                .name(reviewRequest.getName())
                .comment(reviewRequest.getComment())
                .rating(reviewRequest.getRating())
                .createdAt(LocalDateTime.now())
                .saleItem(saleItem)
                .build();
    }
}
