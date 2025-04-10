package bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitPhoneDimensions {
    @NotNull
    private String color;

    @NotNull
    private Double height;

    @NotNull
    private Boolean waterResistance;

    @NotNull
    private Double thickness;

    @NotNull
    private Double weight;

    @NotNull
    private Double width;
}
