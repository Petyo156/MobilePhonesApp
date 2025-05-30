package bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitPhoneRequest {
    //Dimensions
    @Valid
    private SubmitPhoneDimensions dimensions;

    //Brand and Model
    @Valid
    private SubmitBrandAndModel brandAndModel;

    //Hardware
    @Valid
    private SubmitHardware hardware;

    //Camera
    @Valid
    private SubmitCamera camera;

    //Operating system
    @Valid
    private SubmitOperatingSystem operatingSystem;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    @Min(0)
    private Integer quantity;

    @NotNull
    @Min(1800)
    private Integer releaseYear;

    @Nullable
    private String modelUrl;

    @Nullable
    @Size(max = 5000)
    private String description;
}
