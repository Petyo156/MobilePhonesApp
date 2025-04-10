package bg.tu_varna.sit.usp.phone_sales.operatingsystem.model;

import lombok.Getter;

@Getter
public enum OperatingSystemType {
    ANDROID("android"),
    IOS("ios");

    private final String type;

    OperatingSystemType(String type) {
        this.type = type;
    }
}
