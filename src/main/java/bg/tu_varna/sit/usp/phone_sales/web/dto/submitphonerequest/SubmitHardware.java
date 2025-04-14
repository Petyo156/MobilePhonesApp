package bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest;

import bg.tu_varna.sit.usp.phone_sales.hardware.model.SIMType;
import jakarta.annotation.Nullable;
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

    @Nullable
    @Min(0)
    private Integer refreshRate;

    @NotNull
    @Min(0)
    private Double screenSize;

    @NotNull
    private SIMType simType;

    @NotNull
    @Min(0)
    private Integer storage;

    @Nullable
    @Min(0)
    private Integer coreCount;
}
