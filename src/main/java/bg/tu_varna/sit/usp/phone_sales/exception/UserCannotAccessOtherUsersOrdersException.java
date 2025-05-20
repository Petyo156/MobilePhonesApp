package bg.tu_varna.sit.usp.phone_sales.exception;

public class UserCannotAccessOtherUsersOrdersException extends RuntimeException {
    public UserCannotAccessOtherUsersOrdersException(String message) {
        super(message);
    }

    public UserCannotAccessOtherUsersOrdersException(String message, Throwable cause) {
        super(message, cause);
    }
}
