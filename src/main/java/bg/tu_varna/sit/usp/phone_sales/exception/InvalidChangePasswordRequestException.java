package bg.tu_varna.sit.usp.phone_sales.exception;

public class InvalidChangePasswordRequestException extends RuntimeException {
    public InvalidChangePasswordRequestException(String message) {
        super(message);
    }

    public InvalidChangePasswordRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
