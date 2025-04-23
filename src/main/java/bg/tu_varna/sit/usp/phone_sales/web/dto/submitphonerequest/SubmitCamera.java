package bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitCamera {
    @Nullable
    @Min(1)
    private Integer count;

    @NotNull
    @Min(1)
    private Integer resolution;

    @Nullable
    @Min(1)
    private Integer videoResolution;
}
