package bg.tu_varna.sit.usp.phone_sales.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInformationResponse {
    private String firstName;

    private String lastName;

    private String city;

    private String address;

    private String zipCode;

    private String phoneNumber;
}
