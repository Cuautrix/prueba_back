package com.financial.system.vendors;

import com.financial.system.core.models.dto.request.OCRDto;
import com.financial.system.core.models.dto.response.OCRResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IOCRService {
    OCRResponse extractData(OCRDto ocrDto, HttpServletRequest request);
    OCRResponse extractIneData(OCRDto ocrDto);

}
