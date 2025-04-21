package bg.tu_varna.sit.usp.phone_sales.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {
    private String totalPrice;

    private Integer summary;

    private String discountPrice;

    private String discountPercent;
}
