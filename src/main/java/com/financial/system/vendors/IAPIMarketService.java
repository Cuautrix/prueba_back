package com.financial.system.vendors;

import com.financial.system.core.models.dto.response.api_market_responses.RfcResponse;
import com.financial.system.core.models.entities.Client;
import com.financial.system.core.models.entities.ClientValidations;
import jakarta.servlet.http.HttpServletRequest;

public interface IAPIMarketService {
    RfcResponse getRfcData(String curp, HttpServletRequest request);

    RfcResponse getRfcDataContribuidor(String curp);

    Object validateCurp(String curp, Client client);

    Object list69b(String curp, Client client);

    Object validateRfc(String rfc, String name, int regimenClave, String cp, Client client);

    boolean validationsEnabled();
}
