package bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest;

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
public class SubmitCamera {
    @NotNull
    @Min(1)
    private Integer count;

    @NotNull
    @Min(1)
    private Integer resolution;

    @NotNull
    @Min(1)
    private Integer videoResolution;
}
