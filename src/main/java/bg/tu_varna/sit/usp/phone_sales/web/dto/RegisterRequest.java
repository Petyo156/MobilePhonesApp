package bg.tu_varna.sit.usp.phone_sales.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @Size(min = 3, message = "Password must be at least 3 symbols.")
    private String password;

    @Size(min = 3, message = "Password must be at least 3 symbols.")
    private String confirmPassword;

    @Email(message = "Invalid email address.")
    @NotNull
    private String email;
}
