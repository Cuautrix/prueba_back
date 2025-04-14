package com.financial.system.core.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ClientValidationsDto {

    private String lista69b;

    private String rfc;

    private String curp;

    private String datosFiscales;

    private String ocrIne;
}
