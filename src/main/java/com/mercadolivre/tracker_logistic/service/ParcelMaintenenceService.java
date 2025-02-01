package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.status.StatusRecord;
import com.mercadolivre.tracker_logistic.domain.dispatch.DispatchEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelRecord;
import com.mercadolivre.tracker_logistic.repository.DispatchRepository;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ParcelMaintenenceService {

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private DispatchRepository dispatchRepository;

    //Variavel de transações para validação do fluxo de status.
    private Map<String, Set<String>> validTransitions = Map.of(
            "CREATED", Set.of("IN_TRANSIT"),
            "IN_TRANSIT", Set.of("DELIVERED")
    );

    //Responsável por criar um novo pacote
    public ParcelEntity createParcel(ParcelRecord request) {

        Instant now = Instant.now();

        //Preenchendo o Parsel e Dispatch com informações do request
        ParcelEntity parcel = new ParcelEntity();
        parcel.setDescription(request.description());
        parcel.setSender(request.sender());
        parcel.setRecipient(request.recipient());
        parcel.setCreatedAt(now);
        parcel.setUpdatedAt(now);
        parcel.setStatus("CREATED");

        DispatchEntity dispatch = new DispatchEntity();
        dispatch.setParcel(parcel);
        dispatch.setEstimatedDeliveryDate(request.estimatedDeliveryDate());
        dispatch.setHoliday(externalApiService.checkIfHoliday(request.estimatedDeliveryDate()));
        dispatch.setFunFact(externalApiService.getDogFunFact());

        //Salvando os dados na base de dados via repository
        parcelRepository.save(parcel);
        dispatchRepository.save(dispatch);

        return parcel;
    }

    //Responsável por atualizar o status de um pacote
    public ParcelEntity updateParcelStatus(UUID parcelId, StatusRecord statusRecord) {

        String newStatus = statusRecord.status();

        List<String> validStatuses = List.of("CREATED", "IN_TRANSIT", "DELIVERED");
        if (!validStatuses.contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: must be one of CREATED, IN_TRANSIT, DELIVERED");
        }

        ParcelEntity parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"));

        //Metodo para validar a mudança de status
        validateStatusTransition(parcel, newStatus);

        //Atualizando o status do pacote
        if ("DELIVERED".equals(newStatus)) {
            parcel.setDeliveredAt(Instant.now());
        }

        parcel.setStatus(newStatus);
        parcel.setUpdatedAt(Instant.now());
        parcelRepository.save(parcel);

        return parcel;
    }

    //Responsável por cancelar um pacote através do seu ID unico.
    public ParcelEntity cancelParcelById(UUID parcelId) {
        ParcelEntity parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"));

        if ("IN_TRANSIT".equals(parcel.getStatus())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Parcel cannot be cancelled: Only parcels that have not been shipped can be canceled");
        }

        parcel.setStatus("CANCELLED");
        parcel.setUpdatedAt(Instant.now());
        parcelRepository.save(parcel);

        return parcel;
    }

    //Metodo de validação da transição de status
    private void validateStatusTransition(ParcelEntity parcel, String newStatus) {
        String actualStatus = parcel.getStatus();

        if ("CANCELLED".equals(actualStatus)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid status transition: Parcel is already cancelled");
        }

        if ("DELIVERED".equals(actualStatus)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid status transition: Parcel is already delivered");
        }

        if (!validTransitions.getOrDefault(actualStatus, Set.of()).contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status transition: Cannot transition from " + actualStatus + " to " + newStatus);
        }
    }

}
