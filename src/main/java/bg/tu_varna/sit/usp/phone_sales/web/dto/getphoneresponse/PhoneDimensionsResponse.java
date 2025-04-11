package bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDimensionsResponse {
    private String color;

    private Double height;

    private Boolean waterResistance;

    private Double thickness;

    private Double weight;

    private Double width;
}
