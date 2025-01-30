package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.dispatch.DispatchEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelRecord;
import com.mercadolivre.tracker_logistic.repositorie.DispatchRepository;
import com.mercadolivre.tracker_logistic.repositorie.ParcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ParcelMaintenenceService {

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private DispatchRepository dispatchRepository;

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
    public ParcelEntity updateParcelStatus(UUID parcelId, String newStatus) {

        List<String> validStatuses = List.of("CREATED", "IN_TRANSIT", "DELIVERED");
        if (!validStatuses.contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: must be one of CREATED, IN_TRANSIT, DELIVERED");
        }

        ParcelEntity parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"));

        parcel.updateParcelStatus(newStatus);
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
}
