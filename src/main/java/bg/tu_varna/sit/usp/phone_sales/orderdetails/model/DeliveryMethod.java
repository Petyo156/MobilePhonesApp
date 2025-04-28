package bg.tu_varna.sit.usp.phone_sales.orderdetails.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeliveryMethod {
    STANDARD(4.99),
    PICKUP(0.0);

    private final double price;
}