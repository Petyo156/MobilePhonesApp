package bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private String imageUrl;
    private Integer imageIndex;
} 