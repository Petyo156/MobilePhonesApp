package org.example.phone_sales.operatingsystem.model;

import lombok.Getter;

@Getter
public enum OperatingSystemTypeEnum {
    ANDROID("android"),
    IOS("ios");

    private final String type;

    OperatingSystemTypeEnum(String type) {
        this.type = type;
    }
}
