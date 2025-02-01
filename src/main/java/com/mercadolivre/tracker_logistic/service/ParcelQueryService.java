package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.repository.EventRepository;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import com.mercadolivre.tracker_logistic.validation.ParcelValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ParcelQueryService {

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private EventRepository eventRepository;

    //Consulta um pacote pelo seu ID único, incluindo eventos se solicitado
    public ParcelEntity getParcelById(UUID parcelId, boolean includeEvents) {

        ParcelEntity parcel = ParcelValidation.validateParcelExists(parcelRepository, parcelId);

        if (!includeEvents) {
            parcel.setEvents(null);
        }

        return parcel;
    }

    //Consulta uma lista de pacotes com base em filtros de remetente e/ou destinatário se solicitado filtro.
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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parcels found with the provided filters.");
            }
        }
        return parcels;
    }
}
