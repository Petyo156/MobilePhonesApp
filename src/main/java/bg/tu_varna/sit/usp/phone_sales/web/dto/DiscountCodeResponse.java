package bg.tu_varna.sit.usp.phone_sales.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeResponse {
    private String discountCode;

    private String discountPercent;
}
