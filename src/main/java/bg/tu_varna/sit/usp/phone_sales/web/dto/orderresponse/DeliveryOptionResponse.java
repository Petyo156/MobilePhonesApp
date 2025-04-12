package bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse;


import bg.tu_varna.sit.usp.phone_sales.deliveryoption.model.DeliveryMethod;
import bg.tu_varna.sit.usp.phone_sales.deliveryoption.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOptionResponse {
    private String address;

    private String city;

    private DeliveryMethod deliveryMethod;

    private PaymentMethod paymentMethod;

    private String zipCode;
}
