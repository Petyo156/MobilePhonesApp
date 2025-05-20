package bg.tu_varna.sit.usp.phone_sales.exception;

public class InvalidDiscountCodePercentInputException extends RuntimeException {
    public InvalidDiscountCodePercentInputException(String message) {
        super(message);
    }

    public InvalidDiscountCodePercentInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
