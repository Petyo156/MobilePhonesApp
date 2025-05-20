package bg.tu_varna.sit.usp.phone_sales.exception;

public class UserMustBeLoggedInException extends RuntimeException {
    public UserMustBeLoggedInException(String message) {
        super(message);
    }

    public UserMustBeLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
}
