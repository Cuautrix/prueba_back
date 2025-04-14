package com.financial.system.core.services.impl;

import com.financial.system.core.models.dao.*;
import com.financial.system.core.models.dto.ClientTypeDTO;
import com.financial.system.core.models.dto.NationalityDTO;
import com.financial.system.core.models.dto.response.*;
import com.financial.system.core.services.ICatalogService;
import com.financial.system.shared.exceptions.FinancialSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogService implements ICatalogService {


    private final NationalityDAO nationalityDAO;
    private final ClientTypeDAO clientTypeDAO;
    private final EntidadFederativaDao entidadFederativaDao;
    private final GenderDao genderDao;
    private final BanksDao banksDao;
    private final ProductDao productDao;
    private final OccupationDao occupationDAO;
    private final RegimenDao regimenDao;


    @Override
    public List<NationalityDTO> getAllNationalities() {
        return nationalityDAO.findAll().stream()
                .map(n -> {
                    NationalityDTO dto = new NationalityDTO();
                    dto.setId(String.valueOf(n.getId()));
                    dto.setDescription(n.getDescription().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<GenderDto> findAllGenders() {
        log.info("Obteniendo lista de géneros");
        return genderDao.findAll().stream()
                .map(item -> new GenderDto(item.getId(), item.getDescription()))
                .toList();
    }

    @Override
    public List<ClientTypeDTO> getAllTypeClients() {
        return clientTypeDAO.findAll().stream()
                .map(c -> {
                    ClientTypeDTO dto = new ClientTypeDTO();
                    dto.setId(String.valueOf(c.getId()));
                    dto.setDescription(c.getDescription());
                    dto.setKey(String.valueOf(c.getKey()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EntidadFederativaResponse> findEntidadFederativasList() {
        log.info("Listado de entidades federativas iniciado");

        List<EntidadFederativaResponse> response = entidadFederativaDao.findAll()
                .stream()
                .map(item -> new EntidadFederativaResponse(item.getId(), item.getName(), item.getNumCode()))
                .toList();

        log.info("La lista de entidades federativas contiene {} elementos", response.size());
        return response;
    }

    @Override
    public List<OccupationDto> findAllOccupations() {
        log.info("Obteniendo lista de ocupaciones");
        return occupationDAO.findAll().stream()
                .map(item -> new OccupationDto(item.getId(), item.getDescription()))
                .toList();
    }

    @Override
    public List<GenericCatalog> findAllProductTypes() {
        return productDao.getProducts().orElseThrow(() -> new FinancialSystemException("Product types does not exist"));
    }

    public List<GenericCatalog> findAllBanks() {
        return banksDao.getBankActive();
    }

    @Override
    public List<RegimenResponse> findAllRegimens() {
        return regimenDao.fetchRegimens().orElseThrow(() ->
                new FinancialSystemException("Regimens does not exist"));
    }
}
