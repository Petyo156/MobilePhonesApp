package bg.tu_varna.sit.usp.phone_sales.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Size(min = 3, message = "Username must be at least 3 symbols")
    private String username;

    @Size(min = 3, message = "Password must be at least 3 symbols")
    private String password;
}
