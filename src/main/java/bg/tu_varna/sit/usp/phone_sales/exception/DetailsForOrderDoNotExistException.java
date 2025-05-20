package bg.tu_varna.sit.usp.phone_sales.exception;

public class DetailsForOrderDoNotExistException extends RuntimeException {
    public DetailsForOrderDoNotExistException(String message) {
        super(message);
    }

    public DetailsForOrderDoNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
