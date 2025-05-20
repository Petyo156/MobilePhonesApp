package bg.tu_varna.sit.usp.phone_sales.exception;

public class SetAtLeastOnePhonePictureException extends RuntimeException {
    public SetAtLeastOnePhonePictureException(String message) {
        super(message);
    }

    public SetAtLeastOnePhonePictureException(String message, Throwable cause) {
        super(message, cause);
    }
}
