package bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitPhoneDimensions {
    @Size(min = 2, max = 30)
    @NotNull
    private String color;

    private Double height;

    @NotNull
    private Boolean waterResistance;

    private Double thickness;

    private Double weight;

    private Double width;
}
