package bg.tu_varna.sit.usp.phone_sales.exception;

public class InvalidDiscountCodeException extends RuntimeException {
    public InvalidDiscountCodeException(String message) {
        super(message);
    }

    public InvalidDiscountCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
