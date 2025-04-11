package bg.tu_varna.sit.usp.phone_sales.deliveryoption.model;

import lombok.Getter;

@Getter
public enum DeliveryMethod {
    STANDARD("standard"),
    PICKUP("pickup");

    private final String method;

    DeliveryMethod(String method) {
        this.method = method;
    }
}
