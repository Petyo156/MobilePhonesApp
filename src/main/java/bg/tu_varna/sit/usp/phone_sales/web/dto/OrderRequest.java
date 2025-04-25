package bg.tu_varna.sit.usp.phone_sales.web.dto;

import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.DeliveryMethod;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
//    @NotNull
    private PaymentMethod paymentMethod;

//    @NotNull
    private DeliveryMethod deliveryMethod;

//    @NotNull
//    @Size(min = 1, max = 50, message = "First name must be 1-50 symbols long")
    private String firstName;

//    @NotNull
//    @Size(min = 1, max = 50, message = "Last name must be 1-50 symbols long")
    private String lastName;

//    @NotNull
//    @Size(min = 10, max = 100, message = "Address must be 10-100 symbols long")
    private String address;

//    @NotNull
//    @Size(min = 10, max = 10, message = "Phone number must be 10 symbols long")
    private String phoneNumber;

//    @NotNull
//    @Size(min = 10, max = 100, message = "City must be 10-100 symbols long")
    private String city;

//    @Size(min = 4, max = 4, message = "Zip code must be 4 symbols long")
//    @NotNull
    private String zipCode;
}
