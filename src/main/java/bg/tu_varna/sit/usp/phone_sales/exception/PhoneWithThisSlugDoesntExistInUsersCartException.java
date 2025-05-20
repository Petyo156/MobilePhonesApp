package bg.tu_varna.sit.usp.phone_sales.exception;

public class PhoneWithThisSlugDoesntExistInUsersCartException extends RuntimeException {
    public PhoneWithThisSlugDoesntExistInUsersCartException(String message) {
        super(message);
    }

    public PhoneWithThisSlugDoesntExistInUsersCartException(String message, Throwable cause) {
        super(message, cause);
    }
}
