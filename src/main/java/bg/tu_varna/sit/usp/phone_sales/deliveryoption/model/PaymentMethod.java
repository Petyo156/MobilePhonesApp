package bg.tu_varna.sit.usp.phone_sales.deliveryoption.model;

public enum PaymentMethod {
    CASH("cash"),
    CARD("card");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }
}
