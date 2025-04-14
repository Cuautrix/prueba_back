package com.financial.system.core.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegimenResponse {
    private Long id;
    private int clave;
    private String name;
}
