// DTO corregido: ClientFullInfoDTO.java
package com.financial.system.core.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientFullInfoDTO {
    private PersonaFisicaMexDTO personaFisica;
    private List<BankInformationDTO> bankInformation;
    private List<ClientDocumentDTO> documents;
}
