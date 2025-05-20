package bg.tu_varna.sit.usp.phone_sales.exception;

public class PhoneWithThisSlugDoesntExistException extends RuntimeException {
    public PhoneWithThisSlugDoesntExistException(String message) {
        super(message);
    }

    public PhoneWithThisSlugDoesntExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
