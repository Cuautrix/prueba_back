package com.financial.system.core.services;

import com.financial.system.core.models.dto.response.ClientValidationsDto;
import jakarta.servlet.http.HttpServletRequest;


public interface IClientValidationsService {
    ClientValidationsDto getClientValidations(HttpServletRequest request);
    ClientValidationsDto getClientValidationsByClientId(Long clientId);
}
