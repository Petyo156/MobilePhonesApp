package bg.tu_varna.sit.usp.phone_sales.exception;

import lombok.Getter;

@Getter
public class UserHasAlreadyLeftAReviewException extends RuntimeException {
    private final String productSlug;

    public UserHasAlreadyLeftAReviewException(String message, String productSlug) {
        super(message);
        this.productSlug = productSlug;
    }

    public UserHasAlreadyLeftAReviewException(String message, Throwable cause, String productSlug) {
        super(message, cause);
        this.productSlug = productSlug;
    }
}
