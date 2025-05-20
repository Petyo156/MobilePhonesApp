package bg.tu_varna.sit.usp.phone_sales.exception;

public class UserWithThisPhoneNumberAlreadyExistsException extends RuntimeException {
    public UserWithThisPhoneNumberAlreadyExistsException(String message) {
        super(message);
    }

    public UserWithThisPhoneNumberAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
