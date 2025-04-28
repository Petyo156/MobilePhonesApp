package bg.tu_varna.sit.usp.phone_sales.orderdetails.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentMethod {
    CASH(PaymentOptions.PAYMENT_METHOD_CASH_DESCRIPTION),
    CARD(PaymentOptions.PAYMENT_METHOD_CARD_DESCRIPTION);

    private final String description;
}
