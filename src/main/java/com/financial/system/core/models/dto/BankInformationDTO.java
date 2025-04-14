package com.financial.system.core.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankInformationDTO {
    private Long id;
    private String clabe;
    private String accountNumber;
    private Long bankId;
    private Long clientId;
}
