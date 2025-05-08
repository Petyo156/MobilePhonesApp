package bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse;
import bg.tu_varna.sit.usp.phone_sales.order.model.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private String orderNumber;

    private LocalDateTime orderDate;

    private String price;

    private SaleStatus status;

    private List<OrderItemResponse> orderItemResponseList;
}
