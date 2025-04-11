package bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest;

import bg.tu_varna.sit.usp.phone_sales.hardware.model.SIMType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitHardware {
    @NotNull
    @Min(0)
    private Integer batteryCapacity;

    @NotNull
    @Min(0)
    private Integer ram;

    @NotNull
    @Min(0)
    private Integer refreshRate;

    @NotNull
    @Min(0)
    private Integer resolution;

    @NotNull
    @Min(0)
    private Double screenSize;

    @NotNull
    private SIMType simType;

    @NotNull
    @Min(0)
    private Integer storage;

    @NotNull
    @Min(0)
    private Integer coreCount;
}
