package bg.tu_varna.sit.usp.phone_sales.exception;

public class AddStuffToYourCartBeforeCheckingOutException extends RuntimeException {
    public AddStuffToYourCartBeforeCheckingOutException(String message) {
        super(message);
    }

    public AddStuffToYourCartBeforeCheckingOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
