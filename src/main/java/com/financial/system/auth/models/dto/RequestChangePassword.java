package com.financial.system.auth.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestChangePassword {
    @NotBlank(message = "Username is mandatory")
    private String username;
}
