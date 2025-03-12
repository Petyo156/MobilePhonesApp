package org.example.phone_sales.hardware.model;

import lombok.Getter;

@Getter
public enum SIMTypeEnum {
    SINGLE("single"),
    DUAL("dual"),
    ESIM("esim");

    private final String type;

    SIMTypeEnum(String type) {
        this.type = type;
    }

}