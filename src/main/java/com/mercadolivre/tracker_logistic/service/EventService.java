package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import com.mercadolivre.tracker_logistic.domain.event.EventRecord;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.repository.EventRepository;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final ParcelRepository parcelRepository;

    public EventService(EventRepository eventRepository, ParcelRepository parcelRepository) {
        this.eventRepository = eventRepository;
        this.parcelRepository = parcelRepository;
    }

    @Async
    @Transactional
    public void createEvent(EventRecord eventRecord) {
        logger.info("[START] Processando evento para o pacote {}", eventRecord.parcelId());

        ParcelEntity parcel = parcelRepository.findById(eventRecord.parcelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found by ID"));

        EventEntity event = new EventEntity();
        event.setParcel(parcel);
        event.setLocation(eventRecord.location());
        event.setDescription(eventRecord.description());
        event.setDateTime(eventRecord.date());

        eventRepository.save(event);
        logger.info("[END] Evento salvo para o pacote {} na thread {}", eventRecord.parcelId(), Thread.currentThread().getName());
    }
}
