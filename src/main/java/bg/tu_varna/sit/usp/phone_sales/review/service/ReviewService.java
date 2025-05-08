package bg.tu_varna.sit.usp.phone_sales.review.service;

import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.review.model.Review;
import bg.tu_varna.sit.usp.phone_sales.review.repository.ReviewRepository;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ReviewRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ReviewResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void postReview(ReviewRequest reviewRequest, User user, String slug) {
        if(userHasAlreadyLeftAReview(slug, user)){
            throw new DomainException(ExceptionMessages.USER_HAS_ALREADY_LEFT_A_REVIEW);
        }
        Review review = initializeReview(reviewRequest);
        reviewRepository.save(review);
        log.info("Review published successfully");
    }

    public boolean userHasAlreadyLeftAReview(String slug, User user) {
        Optional<Review> reviewOptional = reviewRepository.getReviewBySaleItem_Phone_SlugAndSaleItem_Sale_User(slug, user);
        if(reviewOptional.isEmpty()){
            log.info("User has not left a review");
            return false;
        }
        log.info("User already left a review");
        return true;
    }

    public List<ReviewResponse> getAllReviewsForProduct(String slug) {
        List<Review> reviews = reviewRepository.getReviewsBySaleItem_Phone_Slug(slug);
        List<ReviewResponse> responses = new ArrayList<>();
        for(Review review : reviews){
            ReviewResponse response = initializeReviewResponse(review);
            responses.add(response);
        }
        log.info("Fetched all reviews for given product");
        return responses;
    }

    private ReviewResponse initializeReviewResponse(Review review) {
        return ReviewResponse.builder()
                .name(review.getName())
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }

    private Review initializeReview(ReviewRequest reviewRequest) {
        return Review.builder()
                .name(reviewRequest.getName())
                .comment(reviewRequest.getComment())
                .rating(reviewRequest.getRating())
                .build();
    }
}
