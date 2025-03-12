package org.example.phone_sales.deliveryoption.model;

import lombok.Getter;

@Getter
public enum DeliveryMethodEnum {
    STANDARD("standard"),
    PICKUP("pickup");

    private final String method;

    DeliveryMethodEnum(String method) {
        this.method = method;
    }
}
