package bg.tu_varna.sit.usp.phone_sales.exception;

public class SaleItemDoesNotExistException extends RuntimeException {
    public SaleItemDoesNotExistException(String message) {
        super(message);
    }

    public SaleItemDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
