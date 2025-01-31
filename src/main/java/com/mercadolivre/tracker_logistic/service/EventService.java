package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import com.mercadolivre.tracker_logistic.domain.event.EventRecord;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.repository.EventRepository;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import com.mercadolivre.tracker_logistic.validation.ParcelValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    public void createTrackingEvent(EventRecord eventRecord) {
        ParcelEntity parcel = ParcelValidation.validateParcelExists(parcelRepository, eventRecord.parcelId());

        EventEntity event = new EventEntity();
        event.setParcel(parcel);
        event.setLocation(eventRecord.location());
        event.setDescription(eventRecord.description());
        event.setDateTime(eventRecord.date() != null ? eventRecord.date() : Instant.now());

        eventRepository.save(event);
    }
}
