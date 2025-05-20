package bg.tu_varna.sit.usp.phone_sales.exception;

public class UserWithEmailAlreadyExistsException extends RuntimeException {
    public UserWithEmailAlreadyExistsException(String message) {
        super(message);
    }

    public UserWithEmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
