package com.mercadolivre.tracker_logistic.controller;

import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import com.mercadolivre.tracker_logistic.domain.event.EventRecord;
import com.mercadolivre.tracker_logistic.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    //Responsável por criar um novo evento de rastreamento atrelado a um Pacote por ID.
    @PostMapping
    public ResponseEntity<Void> createTrackingEvent(@RequestBody EventRecord eventRecord) {
        eventService.createTrackingEvent(eventRecord);
        return ResponseEntity.status(204).build();
    }
}
