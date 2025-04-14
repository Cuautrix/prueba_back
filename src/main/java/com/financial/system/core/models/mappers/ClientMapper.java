package com.financial.system.core.models.mappers;

import com.financial.system.core.models.dto.ClientDTO;
import com.financial.system.core.models.dto.RegisterClientDTO;
import com.financial.system.core.models.entities.Client;
import com.financial.system.core.models.entities.ClientType;
import com.financial.system.core.models.entities.ClientValidationStatus;
import com.financial.system.core.models.entities.Nationality;
import com.financial.system.core.models.enums.ClientScope;

import java.time.LocalDateTime;

public abstract class ClientMapper {
    public static Client preRegisterToEntity(RegisterClientDTO request, Nationality nationality, ClientType clientType, ClientValidationStatus clientValidationStatus, ClientScope clientScope, String passwordEncoded) {
        return Client.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoded)
                .nationality(nationality)
                .createdBy(request.getUsername())
                .active(true)
                .clientType(clientType)
                .clientValidationStatus(clientValidationStatus)
                .passwordExpirationDate(LocalDateTime.now().plusMonths(3))
                .privacyNoticeAccepted(request.getPrivacyNoticeAccepted())
                .termsAndConditionsAccepted(request.getTermsAndConditionsAccepted())
                .scope(clientScope.name())
                .createdBy(request.getUsername())
                .rol("cliente")
                .build();
    }

    public static ClientDTO toDTO(Client client) {
        return ClientDTO.builder()
//                .name(client.getName())
                .email(client.getEmail())
//                .lastNameFather(client.getLastNameFather())
//                .lastNameMother(client.getLastNameMother())
                .username(client.getUsername())
                .build();
    }
}
