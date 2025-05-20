package bg.tu_varna.sit.usp.phone_sales.exception;

public class UserHasAlreadyLeftAReviewException extends RuntimeException {
    public UserHasAlreadyLeftAReviewException(String message) {
        super(message);
    }

    public UserHasAlreadyLeftAReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
