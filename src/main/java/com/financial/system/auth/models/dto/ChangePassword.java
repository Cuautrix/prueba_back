package com.financial.system.auth.models.dto;

import com.financial.system.shared.utils.FinancialSystemUtils;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassword {
    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters.")
    private String password;

    @NotBlank(message = "Password confirmation cannot be empty.")
    private String confirmPassword;


    @AssertTrue(message = "Passwords do not match.")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    @AssertTrue(message = "Password does not meet the required criteria.")
    public boolean isPasswordValid() {
        return FinancialSystemUtils.validatePassword(password);
    }
}
