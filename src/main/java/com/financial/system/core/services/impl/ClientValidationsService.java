package com.financial.system.core.services.impl;


import com.financial.system.core.models.dao.ClientDAO;
import com.financial.system.core.models.dao.ClientValidationsDao;
import com.financial.system.core.models.dto.response.ClientValidationsDto;
import com.financial.system.core.models.entities.Client;
import com.financial.system.core.services.IClientValidationsService;
import com.financial.system.security.context.jwt.service.IJWTService;
import com.financial.system.shared.exceptions.FinancialSystemException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientValidationsService implements IClientValidationsService {
    private final IJWTService jwtService;
    private final ClientDAO clientDAO;
    private final ClientValidationsDao clientValidationsDao;

    @Override
    @Transactional
    public ClientValidationsDto getClientValidations(HttpServletRequest request) {
        Client clientFound = jwtService.extractClientFromToken(jwtService.extractTokenFromRequest(request));
        var validationFound = clientValidationsDao.findByClient(clientFound).orElseThrow(() -> new FinancialSystemException("Client validations not found"));
        if (clientFound != null) {
            return ClientValidationsDto.builder()
                    .rfc(validationFound.getRfc())
                    .ocrIne(validationFound.getOcrIne())
                    .curp(validationFound.getCurp())
                    .lista69b(validationFound.getLista69b())
                    .datosFiscales(validationFound.getDatosFiscales())
                    .build();
        }
        return null;
    }

    @Override
    public ClientValidationsDto getClientValidationsByClientId(Long clientId) {
        var clientFound = clientDAO.findById(clientId).orElseThrow(() -> new FinancialSystemException("Client not found"));
        log.info("Client found: " + clientFound.getUsername());
        var validationFound = clientValidationsDao.findByClient(clientFound).orElseThrow(() -> new FinancialSystemException("Client validations not found"));
        if (clientFound != null) {
            return ClientValidationsDto.builder()
                    .rfc(validationFound.getRfc())
                    .ocrIne(validationFound.getOcrIne())
                    .curp(validationFound.getCurp())
                    .lista69b(validationFound.getLista69b())
                    .datosFiscales(validationFound.getDatosFiscales())
                    .build();
        }
        return null;
    }
}
