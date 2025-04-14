package com.financial.system.core.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientBankInformation {
    @NotBlank(message = "clabe is mandatory")
    private String clabe;

    @NotBlank(message = "accountNumber is mandatory")
    private String accountNumber;

    @NotNull(message = "idBank is mandatory")
    private Long bankId;

}
