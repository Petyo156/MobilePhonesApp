package bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private String itemName;

    private Integer quantity;

    private String media;

    private String slug;
}
