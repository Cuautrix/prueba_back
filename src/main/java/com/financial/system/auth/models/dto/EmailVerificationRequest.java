package com.financial.system.auth.models.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailVerificationRequest {
    @NotBlank(message = "EncodedData is required")
    private String encodedData;
}
