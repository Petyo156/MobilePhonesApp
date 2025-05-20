package bg.tu_varna.sit.usp.phone_sales.exception;

public class OrderNumberDoesNotExistException extends RuntimeException {
    public OrderNumberDoesNotExistException(String message) {
        super(message);
    }

    public OrderNumberDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
