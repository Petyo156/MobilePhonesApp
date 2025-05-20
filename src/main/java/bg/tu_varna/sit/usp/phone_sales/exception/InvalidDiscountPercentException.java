package bg.tu_varna.sit.usp.phone_sales.exception;

public class InvalidDiscountPercentException extends RuntimeException {
    public InvalidDiscountPercentException(String message) {
        super(message);
    }

    public InvalidDiscountPercentException(String message, Throwable cause) {
        super(message, cause);
    }
}
