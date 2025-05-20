package bg.tu_varna.sit.usp.phone_sales.exception;

public class ExpectedAuthenticationMetadataPrincipalException extends RuntimeException {
    public ExpectedAuthenticationMetadataPrincipalException(String message) {
        super(message);
    }

    public ExpectedAuthenticationMetadataPrincipalException(String message, Throwable cause) {
        super(message, cause);
    }
}
