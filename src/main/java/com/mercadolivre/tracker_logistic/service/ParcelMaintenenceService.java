package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.status.StatusEnumeration;
import com.mercadolivre.tracker_logistic.domain.status.StatusRecord;
import com.mercadolivre.tracker_logistic.domain.dispatch.DispatchEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelRecord;
import com.mercadolivre.tracker_logistic.repository.DispatchRepository;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ParcelMaintenenceService {

    private static final Logger logger = LoggerFactory.getLogger(ParcelMaintenenceService.class);

    private final ExternalApiService externalApiService;
    private final ParcelRepository parcelRepository;
    private final DispatchRepository dispatchRepository;

    //Variavel de transações para validação do fluxo de status.
    private final Map<StatusEnumeration, Set<StatusEnumeration>> validTransitions = Map.of(
            StatusEnumeration.CREATED, Set.of(StatusEnumeration.IN_TRANSIT),
            StatusEnumeration.IN_TRANSIT, Set.of(StatusEnumeration.DELIVERED)
    );

    public ParcelMaintenenceService(ExternalApiService externalApiService, ParcelRepository parcelRepository, DispatchRepository dispatchRepository) {
        this.externalApiService = externalApiService;
        this.parcelRepository = parcelRepository;
        this.dispatchRepository = dispatchRepository;
    }

    //Responsável por criar um novo pacote
    public ParcelEntity createParcel(ParcelRecord request) {

        logger.info("Iniciando criação de um novo pacote. Remetente: {}, Destinatário: {}", request.sender(), request.recipient());

        Instant now = Instant.now();

        //Preenchendo o Parsel e Dispatch com informações do request
        ParcelEntity parcel = new ParcelEntity();
        parcel.setDescription(request.description());
        parcel.setSender(request.sender());
        parcel.setRecipient(request.recipient());
        parcel.setCreatedAt(now);
        parcel.setUpdatedAt(now);
        parcel.setStatus(StatusEnumeration.CREATED.toString());

        DispatchEntity dispatch = new DispatchEntity();
        dispatch.setParcel(parcel);
        dispatch.setEstimatedDeliveryDate(request.estimatedDeliveryDate());
        dispatch.setHoliday(externalApiService.checkIfHoliday(request.estimatedDeliveryDate()));
        dispatch.setFunFact(externalApiService.getDogFunFact());

        //Salvando os dados na base de dados via repository
        parcelRepository.save(parcel);
        dispatchRepository.save(dispatch);

        logger.info("Pacote criado com sucesso! ID: {}, Status: {}", parcel.getId(), parcel.getStatus());

        return parcel;
    }

    //Responsável por atualizar o status de um pacote
    @CacheEvict(value = "parcels", key = "#parcelId", allEntries = true)
    public ParcelEntity updateParcelStatus(UUID parcelId, StatusRecord statusRecord) {

        String newStatus = String.valueOf(statusRecord.status());

        logger.info("Tentativa de atualização do status do pacote. ID: {}, Novo Status: {}", parcelId, newStatus);

        ParcelEntity parcel = parcelRepository.findParcelWithoutEvents(parcelId)
                .orElseThrow(() -> {
                    logger.error("Pacote não encontrado para atualização. ID: {}", parcelId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found");
                });
        //Metodo para validar a mudança de status
        validateStatusTransition(parcel, StatusEnumeration.valueOf(newStatus));

        //Atualizando o status do pacote
        if (StatusEnumeration.DELIVERED.toString().equals(newStatus)) {
            parcel.setDeliveredAt(Instant.now());
            parcel.setExpiredAt(Instant.now().plus(30, ChronoUnit.DAYS));
            logger.debug("Pacote marcado como entregue. ID: {}, Expira em: 30 dias", parcelId);
        }

        parcel.setStatus(newStatus);
        parcel.setUpdatedAt(Instant.now());
        parcelRepository.save(parcel);

        logger.info("Status do pacote atualizado com sucesso! ID: {}, Novo Status: {}", parcelId, newStatus);
        return parcel;
    }

    //Responsável por cancelar um pacote através do seu ID unico.
    @CacheEvict(value = "parcels", key = "#parcelId", allEntries = true)
    public ParcelEntity cancelParcelById(UUID parcelId) {
        logger.info("Tentativa de cancelamento do pacote. ID: {}", parcelId);

        ParcelEntity parcel = parcelRepository.findParcelWithoutEvents(parcelId)
                .orElseThrow(() -> {
                    logger.error("Pacote não encontrado para cancelamento. ID: {}", parcelId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found by ID");
                });

        if (StatusEnumeration.IN_TRANSIT.toString().equals(parcel.getStatus())) {
            logger.warn("Tentativa de cancelamento de um pacote em trânsito. ID: {}", parcelId);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Parcel cannot be cancelled: Only parcels that have not been shipped can be canceled");
        }

        if (StatusEnumeration.CANCELED.toString().equals(parcel.getStatus())) {
            logger.warn("Tentativa de cancelamento de um pacote já cancelado. ID: {}", parcelId);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "This Parcel is already cancelled");
        }

        parcel.setStatus(StatusEnumeration.CANCELED.toString());
        parcel.setExpiredAt(Instant.now().plus(30, ChronoUnit.DAYS));
        parcel.setUpdatedAt(Instant.now());
        parcelRepository.save(parcel);

        logger.info("Pacote cancelado com sucesso. ID: {}", parcelId);

        return parcel;
    }

    //Metodo de validação da transição de status
    private void validateStatusTransition(ParcelEntity parcel, StatusEnumeration newStatus) {
        StatusEnumeration actualStatus = StatusEnumeration.valueOf(parcel.getStatus());

        if (StatusEnumeration.CANCELED.equals(actualStatus)) {
            logger.warn("Tentativa de transição inválida. Pacote já está cancelado. ID: {}", parcel.getId());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid status transition: Parcel is already cancelled");
        }

        if (StatusEnumeration.DELIVERED.equals(actualStatus)) {
            logger.warn("Tentativa de transição inválida. Pacote já foi entregue. ID: {}", parcel.getId());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid status transition: Parcel is already delivered");
        }

        if (!validTransitions.getOrDefault(actualStatus, Set.of()).contains(newStatus)) {
            logger.error("Tentativa de transição de status inválida. ID: {}, Status Atual: {}, Novo Status: {}", parcel.getId(), actualStatus, newStatus);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status transition: Cannot transition from " + actualStatus + " to " + newStatus);
        }
    }
}
