package com.mercadolivre.tracker_logistic.controller;

import com.mercadolivre.tracker_logistic.domain.event.EventRecord;
import com.mercadolivre.tracker_logistic.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    //Responsável por criar um novo evento de rastreamento atrelado a um Pacote por ID.
    @PostMapping
    public ResponseEntity<String> createTrackingEvent(
            @Valid @RequestBody EventRecord eventRecord) {
        eventService.createEvent(eventRecord);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Event processing started");
    }
}
