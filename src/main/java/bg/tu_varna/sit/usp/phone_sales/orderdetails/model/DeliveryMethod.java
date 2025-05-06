package bg.tu_varna.sit.usp.phone_sales.orderdetails.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum DeliveryMethod {
    STANDARD(DeliveryOptions.DELIVERY_METHOD_STANDARD_DESCRIPTION, DeliveryOptions.DELIVERY_METHOD_STANDARD_PRICE),
    PICKUP(DeliveryOptions.DELIVERY_METHOD_PICKUP_DESCRIPTION, DeliveryOptions.DELIVERY_METHOD_PICKUP_PRICE);

    private final String description;
    private final BigDecimal price;
}
