package bg.tu_varna.sit.usp.phone_sales.exception;

public class InvalidCounterIdException extends RuntimeException {
    public InvalidCounterIdException(String message) {
        super(message);
    }

    public InvalidCounterIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
