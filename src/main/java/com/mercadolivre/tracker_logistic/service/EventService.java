package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import com.mercadolivre.tracker_logistic.domain.event.EventRecord;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.repository.EventRepository;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import com.mercadolivre.tracker_logistic.validation.ParcelValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Async
    @Transactional
    public CompletableFuture<EventEntity> createEvent(EventRecord eventRecord) {
        logger.info("[START] Processando evento para o pacote {}", eventRecord.parcelId());

        ParcelEntity parcel = ParcelValidation.validateParcelExists(parcelRepository, eventRecord.parcelId());

        EventEntity event = new EventEntity();
        event.setParcel(parcel);
        event.setLocation(eventRecord.location());
        event.setDescription(eventRecord.description());
        event.setDateTime(eventRecord.date() != null ? eventRecord.date() : Instant.now());

        eventRepository.save(event);
        logger.info("[END] Evento salvo para o pacote {} na thread {}", eventRecord.parcelId(), Thread.currentThread().getName());

        return CompletableFuture.completedFuture(event);
    }
}
