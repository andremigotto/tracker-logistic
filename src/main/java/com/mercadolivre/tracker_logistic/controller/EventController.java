package com.mercadolivre.tracker_logistic.controller;

import com.mercadolivre.tracker_logistic.domain.event.EventRecord;
import com.mercadolivre.tracker_logistic.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    //Responsável por criar um novo evento de rastreamento atrelado a um Pacote por ID.
    @PostMapping
    public ResponseEntity<Void> createTrackingEvent(
            @Valid @RequestBody EventRecord eventRecord) {
        eventService.createTrackingEvent(eventRecord);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
