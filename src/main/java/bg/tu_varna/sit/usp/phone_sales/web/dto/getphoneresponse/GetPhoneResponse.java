package bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse;

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
public class GetPhoneResponse {
    private PhoneDimensionsResponse dimensions;

    private BrandAndModelResponse brandAndModelResponse;

    private HardwareResponse hardwareResponse;

    private CameraResponse cameraResponse;

    private OperatingSystemResponse operatingSystemResponse;

    private BigDecimal price;

    private Integer quantity;

    private Integer releaseYear;

    private String id;

    private List<String> images;
}
