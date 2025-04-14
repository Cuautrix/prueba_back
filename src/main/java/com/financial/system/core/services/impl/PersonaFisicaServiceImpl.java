package com.financial.system.core.services.impl;

import com.financial.system.core.models.dao.*;
import com.financial.system.core.models.dto.*;
import com.financial.system.core.models.dto.request.BankInformation;
import com.financial.system.core.models.dto.request.ClientBankInformation;
import com.financial.system.core.models.dto.request.pfaemex.PFAEMexDireccionProspectoRequest;
import com.financial.system.core.models.dto.request.pfaemex.PFAEMexPrueba;
import com.financial.system.core.models.dto.request.pfaemex.PersonaFisicaDto;
import com.financial.system.core.models.entities.*;
import com.financial.system.core.models.enums.ClientStatusEnum;
import com.financial.system.core.models.enums.NationalityEnum;
import com.financial.system.core.models.enums.OnboardingStage;
import com.financial.system.core.models.mappers.PersonaFisicaMapper;
import com.financial.system.core.services.IPersonaFisicaService;
import com.financial.system.security.context.jwt.enums.TokenType;
import com.financial.system.security.context.jwt.service.IJWTService;
import com.financial.system.shared.enums.FinancialSystemStatus;
import com.financial.system.shared.exceptions.FinancialSystemException;
import com.financial.system.shared.payload.FinancialSystemResponse;
import com.financial.system.shared.utils.FinancialSystemLogs;
import com.financial.system.vendors.IAPIMarketService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaFisicaServiceImpl implements IPersonaFisicaService {
    private final IJWTService jwtTokenService;
    private final PersonaFisicaDao personaFisicaDao;
    private final GenderDao genderDao;
    private final PersonaFisicaExtranjeraDao personaFisicaExtranjeraDao;
    private final EntidadFederativaDao entidadFederativaDAO;
    private final OccupationDao occupationDAO;
    private final ClientDAO clientDAO;
    private final CountriesDao countriesDao;
    private final IAPIMarketService apiMarketService;
    private final BankInformationDAO bankInformationDAO;
    private final ClientDocumentDAO clientDocumentDAO;

    private final ClientVSDAO validationStatusDAO;


    @Override
    public FinancialSystemResponse saveDireccionProspecto(HttpServletRequest servletRequest, PFAEMexDireccionProspectoRequest request) throws FinancialSystemException {
        Client client = jwtTokenService.extractClientFromToken(jwtTokenService.extractTokenFromRequest(servletRequest));

        PersonaFisicaMex existingClient = personaFisicaDao.findByClientId(client.getId())
                .orElse(new PersonaFisicaMex());

//        SingleMapResponse localCountryResponse = aurumCoreService.findLocalCountry();

//        var country = countriesDao.findByName("Mexico").orElseThrow(() -> new FinancialSystemException("Country not found"));

//        existingClient.setPaisOrigen(Long.valueOf(localCountryResponse.getCode()));
        existingClient.setPaisOrigen(request.getPaisDomicilio());
        existingClient.setCodigoPostal(request.getCp());
        existingClient.setColonia(request.getColonia());
        existingClient.setMunicipio(request.getMunicipio());
        existingClient.setNumeroInterior(request.getInterior());
        existingClient.setNumeroExterior(request.getNumero());
        existingClient.setCiudad(request.getCiudad());
        existingClient.setCalle(request.getCalle());
        existingClient.setIdsuburb(request.getColonia());

//        updateTrace(existingClient);

        personaFisicaDao.save(existingClient);

        if (!ClientStatusEnum.WITH_OBSERVATIONS.equals(client.getClientValidationStatus().getDescription())
                && !client.getOnboardingStages().contains(OnboardingStage.CARGA_ARCHIVOS.name())) {
            log.info("Updating onboarding stages, current stage {}", client.getOnboardingStages());
            String currentStages = client.getOnboardingStages().concat(" ").concat(OnboardingStage.CARGA_ARCHIVOS.name());
            client.setOnboardingStages(currentStages);
            clientDAO.saveAndFlush(client);
            log.info("Updating onboarding stages, new stage {}", client.getOnboardingStages());
        }

        log.info("Client information was updated");

        log.info("Generating new token with updated stage");

        String token = jwtTokenService.generateToken(client.getId(), 300000, TokenType.ACCESS_TOKEN.name(), client);
        FinancialSystemResponse response = new FinancialSystemResponse("Informacion guardada", token, FinancialSystemStatus.OK, 200);
        log.info("Sending response: {}", response);
        return response;
    }

    @Override
    public PersonaFisicaDto findPersonaFisicaInformation(HttpServletRequest servletRequest) throws FinancialSystemException {
        log.info(jwtTokenService.extractTokenFromRequest(servletRequest));
        Client client = jwtTokenService.extractClientFromToken(jwtTokenService.extractTokenFromRequest(servletRequest));

        PersonaFisicaMex personaFisicaMex = personaFisicaDao.findByClientId(client.getId()).orElse(null);
        PersonaFisicaExtranjera personaFisicaExtranjera = personaFisicaExtranjeraDao.findByClientId(client.getId()).orElse(null);

        if (NationalityEnum.MEXICAN.equals(client.getNationality().getDescription())) {

            if (personaFisicaMex == null) {
                return new PersonaFisicaDto(client.getEmail());
            }

            return PersonaFisicaMapper.toResponse(personaFisicaMex, client.getEmail());
        } else {
            if (personaFisicaExtranjera == null) {
                return new PersonaFisicaDto(client.getEmail());
            }

            return PersonaFisicaMapper.toResponse(personaFisicaExtranjera, client.getEmail());
        }

    }

    @Override
    public ClientFullInfoDTO findClientFullInfoById(Long clientId) {
        PersonaFisicaMex personaFisica = personaFisicaDao.findByClientId(clientId).orElse(null);
        List<BankInformation> banks = bankInformationDAO.findAllByClientId(clientId);
        List<ClientDocument> documents = clientDocumentDAO.findByClientId(clientId);

        // Convertir entidad a DTO plano
        PersonaFisicaMexDTO personaFisicaDTO = null;
        if (personaFisica != null) {
            personaFisicaDTO = new PersonaFisicaMexDTO(
                    personaFisica.getBirthDate(),
                    personaFisica.getPhone(),
                    personaFisica.getClientId(),
                    personaFisica.getPaisOrigen(),
                    personaFisica.getName(),
                    personaFisica.getLastName(),
                    personaFisica.getSecondLastName(),
                    personaFisica.getCodigoPostal(),
                    personaFisica.getColonia(),
                    personaFisica.getMunicipio(),
                    personaFisica.getCiudad(),
                    personaFisica.getCalle(),
                    personaFisica.getNumeroExterior(),
                    personaFisica.getNumeroInterior(),
                    personaFisica.getCurp(),
                    personaFisica.getRfc(),
                    personaFisica.getIdsuburb()
            );
        }

        List<BankInformationDTO> bankDTOs = banks.stream().map(b -> new BankInformationDTO(
                b.getId(),
                b.getClabe(),
                b.getAccountNumber(),
                b.getBankId(),
                b.getClientId()
        )).toList();

        List<ClientDocumentDTO> documentDTOs = documents.stream().map(d -> new ClientDocumentDTO(
                d.getId(),
                d.getClientId(),
                d.getDocumentType(),
                d.getFileName(),
                d.getFilePath(),
                d.getFileExtension(),
                d.getUploadedAt()
        )).toList();

        return new ClientFullInfoDTO(personaFisicaDTO, bankDTOs, documentDTOs);
    }







    @Override
    public FinancialSystemResponse saveClientBankInformation(HttpServletRequest servletRequest, ClientBankInformation request)
            throws FinancialSystemException {

        // 1. Obtener cliente desde el token
        Client client = jwtTokenService.extractClientFromToken(
                jwtTokenService.extractTokenFromRequest(servletRequest)
        );

        // 2. Buscar si ya tiene información bancaria, si no crear nueva
        BankInformation bankInfo = bankInformationDAO.findByClientId(client.getId())
                .orElse(new BankInformation());

        // 3. Asignar datos del request
        bankInfo.setClientId(client.getId());
        bankInfo.setClabe(request.getClabe());
        bankInfo.setAccountNumber(request.getAccountNumber());
        bankInfo.setBankId(request.getBankId());

        // 4. Guardar en la base de datos
        bankInformationDAO.save(bankInfo);

        // 5. Actualizar etapa de onboarding si aplica
        if (!ClientStatusEnum.WITH_OBSERVATIONS.equals(client.getClientValidationStatus().getDescription())
                && !client.getOnboardingStages().contains(OnboardingStage.CARGA_ARCHIVOS.name())) {
            log.info("Updating onboarding stages, current stage {}", client.getOnboardingStages());
            String currentStages = client.getOnboardingStages()
                    .concat(" ")
                    .concat(OnboardingStage.CARGA_ARCHIVOS.name());
            client.setOnboardingStages(currentStages);
            clientDAO.saveAndFlush(client);
            log.info("Updating onboarding stages, new stage {}", client.getOnboardingStages());
        }

        // 6. Log de éxito
        log.info("Client bank information was saved successfully");

        // 7. Generar nuevo token actualizado
        log.info("Generating new token with updated stage");
        String token = jwtTokenService.generateToken(
                client.getId(), 300000, TokenType.ACCESS_TOKEN.name(), client
        );

        // 8. Enviar respuesta
        FinancialSystemResponse response = new FinancialSystemResponse("Información bancaria guardada", token, FinancialSystemStatus.OK, 200);
        log.info("Sending response: {}", response);
        return response;
    }


    @Override
    public FinancialSystemResponse saveDatosProspecto(HttpServletRequest servletRequest, PersonaFisicaDto request) throws FinancialSystemException {
        Client client = jwtTokenService.extractClientFromToken(jwtTokenService.extractTokenFromRequest(servletRequest));
        log.info(FinancialSystemLogs.LOG_SEPARATOR);
        log.info("hola");
        PersonaFisicaMex personaFisicaByCurp = personaFisicaDao.findByCurpAndClientIdNot((request.getCurp()), client.getId()).stream().findAny().orElse(null);
        if (Objects.nonNull(personaFisicaByCurp)) {
            log.error("Record could not be saved. CURP already used.");
            throw new FinancialSystemException("Ya existe un registro con la CURP ingresada.");
        }

        PersonaFisicaMex personaFisicaByRfc = personaFisicaDao.findByRfcAndClientIdNot(request.getRfc(), client.getId()).stream().findAny().orElse(null);
        if (Objects.nonNull(personaFisicaByRfc)) {
            log.error("Record could not be saved. RFC already used.");
            throw new FinancialSystemException("Ya existe un registro con el RFC ingresado.");
        }

        PersonaFisicaMex personaFisicaByPhone = personaFisicaDao.findByPhoneAndClientIdNot(request.getTelefono(), client.getId()).stream().findAny().orElse(null);
        if (Objects.nonNull(personaFisicaByPhone)) {
            log.error("Record could not be saved. PHONE NUMBER already used.");
            throw new FinancialSystemException("Ya existe un registro con el numero de telefono ingresado.");
        }

        log.info("Updating information for client with Id: {}", client.getId());

        var gender = genderDao.findById(request.getGenero())
                .orElseThrow(() -> new FinancialSystemException("Gender not found"));

        var entidadFederativa = entidadFederativaDAO.findById(request.getEntidadNacimiento())
                .orElseThrow(() -> new FinancialSystemException("Entidad Federativa not found"));

        if (Objects.isNull(entidadFederativa.getCode())) {
            throw new FinancialSystemException("Entidad Federativa " + entidadFederativa.getName() + " no tiene codigo de estado.");
        }

        var ocupacion = occupationDAO.findById(request.getGiro())
                .orElseThrow(() -> new FinancialSystemException("Ocupacion not found"));

        if (this.apiMarketService.validationsEnabled()) {
            processIdentityValidations(request, client);
        }

        if (NationalityEnum.MEXICAN.equals(client.getNationality().getDescription())) {
            PersonaFisicaMex personaFisica = personaFisicaDao.findByClientId(client.getId()).orElse(new PersonaFisicaMex());

            personaFisica.setClientId(client.getId());
            personaFisica.setGender(gender);
            personaFisica.setEntidadFederativa(entidadFederativa);
            personaFisica.setOccupation(ocupacion);
            personaFisica.setName(request.getNombre());
            personaFisica.setLastName(request.getPrimerApellido());
            personaFisica.setSecondLastName(request.getSegundoApellido());

            LocalDate date = LocalDate.parse(request.getFechaNacimiento());

            personaFisica.setBirthDate(date); // BUG la fecha se guarda con 1 dia menos
            personaFisica.setPhone(request.getTelefono());
            personaFisica.setCurp(request.getCurp());
            personaFisica.setRfc(request.getRfc());
            personaFisica.setCreatedBy(client.getUsername());

            log.info("Updating client with next information: {}", personaFisica);

            personaFisicaDao.save(personaFisica);

        } else {
            PersonaFisicaExtranjera personaFisica = personaFisicaExtranjeraDao.findByClientId(client.getId()).orElse(new PersonaFisicaExtranjera());

            personaFisica.setClientId(client.getId());
                personaFisica.setCreatedBy(client.getUsername());
            personaFisica.setGender(gender);
            personaFisica.setEntidadFederativaId(entidadFederativa.getId());
            personaFisica.setOccupation(ocupacion);
            personaFisica.setName(request.getNombre());
            personaFisica.setLastName(request.getPrimerApellido());
            personaFisica.setSecondLastName(request.getSegundoApellido());

            LocalDate date = LocalDate.parse(request.getFechaNacimiento());

            personaFisica.setBirthDate(date); // BUG la fecha se guarda con 1 dia menos
            personaFisica.setPhone(request.getTelefono());
            personaFisica.setCurp(request.getCurp());
            personaFisica.setRfc(request.getRfc());
            personaFisica.setCreatedBy(client.getUsername());

            log.info("Updating client with next information: {}", personaFisica);

            personaFisicaExtranjeraDao.save(personaFisica);
        }

        if (!ClientStatusEnum.WITH_OBSERVATIONS.equals(client.getClientValidationStatus().getDescription())
                && !client.getOnboardingStages().contains(OnboardingStage.INFO_BANCARIA.name())) {
            log.info("Updating onboarding stages, current stage {}", client.getOnboardingStages());
            String currentStages = client.getOnboardingStages().concat(" ").concat(OnboardingStage.INFO_BANCARIA.name());
            client.setOnboardingStages(currentStages);
            clientDAO.saveAndFlush(client);
            log.info("Updating onboarding stages, new stage {}", client.getOnboardingStages());
        }

        String token = jwtTokenService.generateToken(client.getId(), 300000, TokenType.ACCESS_TOKEN.name(), client);

        FinancialSystemResponse response = new FinancialSystemResponse("Informacion guardada", token, FinancialSystemStatus.OK, 200);
        log.info("Sending response: {}", response);
        return response;
    }

    public void processIdentityValidations(PersonaFisicaDto request, Client client) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        futures.add(CompletableFuture.runAsync(() -> apiMarketService.validateCurp(request.getCurp(), client)));
        futures.add(CompletableFuture.runAsync(() -> apiMarketService.list69b(request.getRfc(), client)));
        futures.add(CompletableFuture.runAsync(() -> apiMarketService.validateRfc(request.getRfc(), request.getNombre() + " " + request.getPrimerApellido() + " " + request.getSegundoApellido(), request.getRegimenId(), request.getCp(), client)));


        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allFutures.thenRun(() -> {
            log.info("Todas las validaciones se completaron.");
        }).exceptionally(ex -> {
            log.error("Error durante las validaciones: {}", ex.getMessage());
            return null;
        }).join();
    }

    @Override
    public FinancialSystemResponse uploadDocuments(HttpServletRequest request, List<UploadDocumentRequest> documents)
            throws FinancialSystemException {

        // Extraer cliente desde token
        Client client = jwtTokenService.extractClientFromToken(
                jwtTokenService.extractTokenFromRequest(request)
        );

        List<ClientDocument> savedDocs = new ArrayList<>();
        String clientStoragePath = "storage/clientes/" + client.getId();

        try {
            Files.createDirectories(Paths.get(clientStoragePath)); // Asegura que exista el directorio
        } catch (IOException e) {
            log.error("No se pudo crear el directorio de almacenamiento para el cliente: " + client.getId(), e);
        }

        for (UploadDocumentRequest doc : documents) {
            String extension = doc.getFilename().substring(doc.getFilename().lastIndexOf('.') + 1);
            String filePath = clientStoragePath + "/" + doc.getFilename();

            try {
                byte[] decodedBytes = Base64.getDecoder().decode(doc.getBase64FileContent());
                Files.write(Paths.get(filePath), decodedBytes);
            } catch (IOException e) {
                log.error("Error al guardar el archivo: " + doc.getFilename(), e);
                continue; // Salta al siguiente documento si falla este
            }

            // Crear y guardar la entidad del documento
            ClientDocument entity = new ClientDocument();
            entity.setClientId(client.getId());
            entity.setDocumentType(doc.getKey());
            entity.setFileName(doc.getFilename());
            entity.setFileExtension("." + extension);
            entity.setFilePath(filePath);

            savedDocs.add(clientDocumentDAO.save(entity));
        }

        String newToken = jwtTokenService.generateToken(client.getId(), 300000, TokenType.ACCESS_TOKEN.name(), client);

        return new FinancialSystemResponse(
                "Documentos guardados exitosamente",
                Map.of("token", newToken),
                FinancialSystemStatus.OK,
                200
        );
    }




    @Override
    public void updateValidationStatus(ClientVSrequest request) {
        log.info("🔍 Iniciando actualización de validación para cliente ID: {}", request.getClientId());

        Optional<ClientVS> optionalStatus = validationStatusDAO.findByClientId(request.getClientId());
        if (optionalStatus.isEmpty()) {
            log.warn("⚠️ No se encontró el registro de validación para client_id {}", request.getClientId());
            throw new FinancialSystemException("Cliente no encontrado");
        }

        ClientVS status = optionalStatus.get();


        log.info("🔎 Estado actual antes de actualizar: {}", status);

        status.setBankInfo(request.isBankInfo());
        status.setAddressInfo(request.isAddressInfo());
        status.setPersonalInfo(request.isPersonalInfo());
        status.setDocuments(request.isDocuments());

        log.info("✅ Nuevos valores: bankInfo={}, addressInfo={}, personalInfo={}, documents={}",
                request.isBankInfo(), request.isAddressInfo(), request.isPersonalInfo(), request.isDocuments());

        validationStatusDAO.save(status);
        log.info("💾 Estado de validación actualizado correctamente para client_id {}", request.getClientId());
    }








    @Override
    public List<PFAEMexPrueba> findAllProspects() throws FinancialSystemException {
        List<PersonaFisicaMex> personasFisicas = personaFisicaDao.findAll();
        return personasFisicas.stream()
                .map(persona -> {
                    PFAEMexPrueba pfae = PFAEMexPrueba.builder()
                            .rfc(persona.getRfc())
                            .nombre(persona.getName())
                            .primerApellido(persona.getLastName())
                            .segundoApellido(persona.getSecondLastName())
                            .id(persona.getClientId())
                            .build();
                    return pfae;
                })
                .collect(Collectors.toList());
    }
}
