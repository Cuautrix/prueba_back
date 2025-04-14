package com.financial.system.core.models.dto;

import com.financial.system.shared.utils.FinancialSystemUtils;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterClientDTO implements Serializable {
    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters.")
    private String password;

    @NotBlank(message = "Password confirmation cannot be empty.")
    private String confirmPassword;

    private Long idClientType;

    @NotNull(message = "idNationality is mandatory.")
    private Long idNationality;

    @NotNull(message = "Privacy Noticed is mandatory.")
    private Boolean privacyNoticeAccepted;

    private Boolean termsAndConditionsAccepted;

    @AssertTrue(message = "Passwords do not match.")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    @AssertTrue(message = "Password does not meet the required criteria.")
    public boolean isPasswordValid() {
        return FinancialSystemUtils.validatePassword(password);
    }
}
