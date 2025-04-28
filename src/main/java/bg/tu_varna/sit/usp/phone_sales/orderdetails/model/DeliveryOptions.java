package bg.tu_varna.sit.usp.phone_sales.orderdetails.model;

import java.math.BigDecimal;

public class DeliveryOptions {
    static final String DELIVERY_METHOD_STANDARD_DESCRIPTION = "Standard Delivery";
    static final String DELIVERY_METHOD_PICKUP_DESCRIPTION = "Store Pickup";
    static final BigDecimal DELIVERY_METHOD_STANDARD_PRICE = BigDecimal.valueOf(4.99);
    static final BigDecimal DELIVERY_METHOD_PICKUP_PRICE = BigDecimal.ZERO;
}
