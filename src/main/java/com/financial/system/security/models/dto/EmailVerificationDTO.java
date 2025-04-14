package com.financial.system.security.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationDTO {
    private String code;
    private String email;
    private Long clientId;
}
