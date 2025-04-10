package bg.tu_varna.sit.usp.phone_sales.hardware.model;

import lombok.Getter;

@Getter
public enum SIMType {
    SINGLE("single"),
    DUAL("dual"),
    ESIM("esim");

    private final String type;

    SIMType(String type) {
        this.type = type;
    }

}