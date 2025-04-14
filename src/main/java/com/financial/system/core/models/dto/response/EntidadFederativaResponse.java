package com.financial.system.core.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntidadFederativaResponse {
    private Long id;
    private String name;
    private Long code;
}
