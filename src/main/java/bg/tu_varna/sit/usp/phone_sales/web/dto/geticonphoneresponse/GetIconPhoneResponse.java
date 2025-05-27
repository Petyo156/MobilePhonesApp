package bg.tu_varna.sit.usp.phone_sales.web.dto.geticonphoneresponse;

import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetIconPhoneResponse {
    private String name;

    private String slug;

    private String price;

    private String discountPrice;

    private String discountPercent;

    private List<ImageResponse> images;

    private BigDecimal rating;
}
