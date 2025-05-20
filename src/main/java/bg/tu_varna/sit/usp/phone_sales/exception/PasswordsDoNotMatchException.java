package bg.tu_varna.sit.usp.phone_sales.exception;

public class PasswordsDoNotMatchException extends RuntimeException {
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }

    public PasswordsDoNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
