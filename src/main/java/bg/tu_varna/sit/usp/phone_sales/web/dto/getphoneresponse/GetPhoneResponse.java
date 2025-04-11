package bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

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

    private Double price;

    private Integer quantity;

    private Integer releaseYear;

    private UUID id;
}
