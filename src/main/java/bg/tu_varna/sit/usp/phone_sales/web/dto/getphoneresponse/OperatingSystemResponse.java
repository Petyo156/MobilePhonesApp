package bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse;

import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperatingSystemResponse {
    private OperatingSystemType operatingSystemType;

    private String version;
}
