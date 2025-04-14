package com.financial.system.core.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientVSrequest {
    private Long clientId;
    private boolean bankInfo;
    private boolean addressInfo;
    private boolean personalInfo;
    private boolean documents;
}
