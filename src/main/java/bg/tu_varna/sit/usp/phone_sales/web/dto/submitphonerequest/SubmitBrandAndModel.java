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
public class SubmitBrandAndModel {
    @NotNull
    @Size(min = 2, max = 20)
    private String brand;

    @NotNull
    @Size(min = 2, max = 20)
    private String model;
}
