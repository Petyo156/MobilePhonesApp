package bg.tu_varna.sit.usp.phone_sales.web.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @Size(min = 3, message = "Password must be at least 3 symbols.")
    private String oldPassword;

    @Size(min = 3, message = "Password must be at least 3 symbols.")
    private String newPassword;

    @Size(min = 3, message = "Password must be at least 3 symbols.")
    private String confirmNewPassword;
}
