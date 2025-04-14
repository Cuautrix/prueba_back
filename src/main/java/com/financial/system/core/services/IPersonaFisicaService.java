package com.financial.system.core.services;

import com.financial.system.core.models.dto.ClientFullInfoDTO;
import com.financial.system.core.models.dto.ClientVSrequest;
import com.financial.system.core.models.dto.UploadDocumentRequest;
import com.financial.system.core.models.dto.request.ClientBankInformation;
import com.financial.system.core.models.dto.request.pfaemex.PFAEMexDireccionProspectoRequest;
import com.financial.system.core.models.dto.request.pfaemex.PFAEMexPrueba;
import com.financial.system.core.models.dto.request.pfaemex.PersonaFisicaDto;
import com.financial.system.core.models.dto.request.pfaemex.UploadDocumentsRequest;
import com.financial.system.shared.exceptions.FinancialSystemException;
import com.financial.system.shared.payload.FinancialSystemResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IPersonaFisicaService {
    List<PFAEMexPrueba> findAllProspects() throws FinancialSystemException;
    PersonaFisicaDto findPersonaFisicaInformation(HttpServletRequest servletRequest) throws FinancialSystemException;
    FinancialSystemResponse saveDatosProspecto(HttpServletRequest servletRequest, PersonaFisicaDto request) throws FinancialSystemException;
    FinancialSystemResponse saveDireccionProspecto(HttpServletRequest servletRequest, PFAEMexDireccionProspectoRequest request) throws FinancialSystemException;
    FinancialSystemResponse saveClientBankInformation(HttpServletRequest servletRequest, ClientBankInformation request) throws FinancialSystemException;
    FinancialSystemResponse uploadDocuments(HttpServletRequest servletRequest, List<UploadDocumentRequest> documents);
    ClientFullInfoDTO findClientFullInfoById(Long clientId);
    void updateValidationStatus(ClientVSrequest request);

}
