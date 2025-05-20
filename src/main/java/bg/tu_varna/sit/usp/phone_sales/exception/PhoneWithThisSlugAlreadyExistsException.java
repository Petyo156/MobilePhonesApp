package bg.tu_varna.sit.usp.phone_sales.exception;

public class PhoneWithThisSlugAlreadyExistsException extends RuntimeException {
    public PhoneWithThisSlugAlreadyExistsException(String message) {
        super(message);
    }

    public PhoneWithThisSlugAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
