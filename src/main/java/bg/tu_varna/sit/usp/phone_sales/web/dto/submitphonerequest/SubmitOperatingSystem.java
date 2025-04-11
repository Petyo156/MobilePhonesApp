package bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest;

import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystemType;
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
public class SubmitOperatingSystem {
    @NotNull
    private OperatingSystemType operatingSystemType;

    @NotNull
    @Size(min = 1, max = 30)
    private String version;
}
