package com.financial.system.core.services;

import com.financial.system.core.models.dto.response.ZipCodeAddress;
import com.financial.system.shared.exceptions.FinancialSystemException;

public interface IZipCodeAddressService {
    ZipCodeAddress obtainZipCodeInformation(String zipCode) throws FinancialSystemException;
}
