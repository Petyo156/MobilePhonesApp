package bg.tu_varna.sit.usp.phone_sales.exception;

public class UserWithEmailDoesntExistException extends RuntimeException {
    public UserWithEmailDoesntExistException(String message) {
        super(message);
    }

    public UserWithEmailDoesntExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
