package org.example.phone_sales.deliveryoption.model;

public enum PaymentMethodEnum {
    CASH("cash"),
    CARD("card");

    private final String method;

    PaymentMethodEnum(String method) {
        this.method = method;
    }
}
