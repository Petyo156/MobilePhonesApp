package bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse;

import bg.tu_varna.sit.usp.phone_sales.hardware.model.SIMType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardwareResponse {
    private Integer batteryCapacity;

    private Integer ram;

    private Integer refreshRate;

    private String screenResolution;

    private Double screenSize;

    private SIMType simType;

    private Integer storage;

    private Integer coreCount;
}
