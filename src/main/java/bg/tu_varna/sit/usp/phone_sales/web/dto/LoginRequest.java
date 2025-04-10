package bg.tu_varna.sit.usp.phone_sales.web.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class LoginRequest {

    @Size(min = 3, message = "Email must be at least 3 symbols")
    private String email;

    @Size(min = 3, message = "Password must be at least 3 symbols")
    private String password;


}
