package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelDTO;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.domain.dispatch.DispatchEntity;
import com.mercadolivre.tracker_logistic.repositorie.DispatchRepository;
import com.mercadolivre.tracker_logistic.repositorie.EventRepository;
import com.mercadolivre.tracker_logistic.repositorie.ParcelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ParcelService {

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private DispatchRepository dispatchRepository;

    @Autowired
    private EventRepository eventRepository;

    public ParcelEntity createParcel(ParcelDTO request) {

        Instant now = Instant.now();

        //Preenchendo o Parsel e Dispatch com informações do request
        ParcelEntity parcel = new ParcelEntity();
        parcel.setDescription(request.getDescription());
        parcel.setSender(request.getSender());
        parcel.setRecipient(request.getRecipient());
        parcel.setCreatedAt(now);
        parcel.setUpdatedAt(now);
        parcel.setStatus("CREATED");

        DispatchEntity dispatch = new DispatchEntity();
        dispatch.setParcel(parcel);
        dispatch.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());
        dispatch.setHoliday(externalApiService.checkIfHoliday(request.getEstimatedDeliveryDate()));
        dispatch.setFunFact(externalApiService.getDogFunFact());

        //Salvando os dados na base de dados via repository
        parcelRepository.save(parcel);
        dispatchRepository.save(dispatch);

        return parcel;
    }

    public ParcelEntity getParcelById(UUID parcelId, boolean includeEvents) {

        ParcelEntity parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"));

        if (includeEvents) {
            List<EventEntity> events = eventRepository.findByParcelId(parcelId);
            parcel.setEvents(events);
        }
        return parcel;
    }

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

    public List<ParcelEntity> getParcelsByFilter(String sender, String recipient) {
        List<ParcelEntity> parcels;

        if (sender != null && recipient != null) {
            parcels = parcelRepository.findBySenderAndRecipient(sender, recipient);
            if (parcels.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parcels found for the given sender and recipient");
            }
        } else if (sender != null) {
            parcels = parcelRepository.findBySender(sender);
            if (parcels.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parcels found for sender: " + sender);
            }
        } else if (recipient != null) {
            parcels = parcelRepository.findByRecipient(recipient);
            if (parcels.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parcels found for recipient: " + recipient);
            }
        } else {
            parcels = parcelRepository.findAll();
            if (parcels.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parcels available");
            }
        }
        return parcels;
    }
}
