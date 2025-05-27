package bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPhoneResponse {
    private PhoneDimensionsResponse dimensions;

    private BrandAndModelResponse brandAndModelResponse;

    private HardwareResponse hardwareResponse;

    private CameraResponse cameraResponse;

    private OperatingSystemResponse operatingSystemResponse;

    private String price;

    private String discountPrice;

    private String discountPercent;

    private Integer quantity;

    private Integer releaseYear;

    private String slug;

    private String modelUrl;

    private List<ImageResponse> images;

    private LocalDateTime createdAt;

    private Boolean isVisible;

    private BigDecimal rating;

    private String description;

    private String name;
}
