package bg.tu_varna.sit.usp.phone_sales.exception;

public class FailedToUploadImageToCloudinaryException extends RuntimeException {
    public FailedToUploadImageToCloudinaryException(String message) {
        super(message);
    }

    public FailedToUploadImageToCloudinaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
