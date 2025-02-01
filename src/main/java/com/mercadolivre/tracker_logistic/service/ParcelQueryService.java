package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.repository.EventRepository;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ParcelQueryService {

    Logger log = LoggerFactory.getLogger(ParcelQueryService.class);

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private EventRepository eventRepository;

    //Consulta um pacote pelo seu ID único, excluindo eventos se solicitado
    @Cacheable(value = "parcels", key = "#id")
    public ParcelEntity getParcelById(UUID id, boolean includeEvents) {
        return includeEvents
                ? parcelRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"))
                : parcelRepository.findParcelWithoutEvents(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"));
    }

    //Consulta uma lista de pacotes com base em filtros de remetente e/ou destinatário se solicitado filtro.
    public Page<ParcelEntity> getParcelsByFilter(String sender, String recipient, Pageable pageable) {
        Page<ParcelEntity> parcels =
                (sender != null && recipient != null) ? parcelRepository.findBySenderAndRecipientWithoutEvents(sender, recipient, pageable) :
                        (sender != null) ? parcelRepository.findBySenderWithoutEvents(sender, pageable) :
                                (recipient != null) ? parcelRepository.findByRecipientWithoutEvents(recipient, pageable) :
                                        parcelRepository.findAllWithoutEvents(pageable);

        if (parcels.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parcels found with the provided filters.");
        }
        return parcels;
    }

}
