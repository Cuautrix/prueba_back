package com.financial.system.core.services;


import com.financial.system.core.models.dto.ClientTypeDTO;
import com.financial.system.core.models.dto.NationalityDTO;
import com.financial.system.core.models.dto.response.*;

import java.util.List;

public interface ICatalogService {
    List<NationalityDTO> getAllNationalities();
    List<ClientTypeDTO> getAllTypeClients();
    List<EntidadFederativaResponse> findEntidadFederativasList();
    List<GenderDto> findAllGenders();
    List<OccupationDto> findAllOccupations();
    List<GenericCatalog> findAllBanks();
    List<GenericCatalog> findAllProductTypes();
    List<RegimenResponse> findAllRegimens();
}