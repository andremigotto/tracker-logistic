package com.mercadolivre.tracker_logistic.controller;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelDTO;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.service.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

    @PostMapping
    public ResponseEntity<ParcelEntity> createParcel(@RequestBody ParcelDTO parcelRequest) {
        ParcelEntity parcel = parcelService.createParcel(parcelRequest);
        return ResponseEntity.status(201).body(parcel);
    }

    @GetMapping("/{parcelId}")
    public ResponseEntity<ParcelEntity> getParcelById(
            @PathVariable("parcelId") UUID id,
            @RequestParam(defaultValue = "true") boolean showEvents) {
        return ResponseEntity.ok(parcelService.getParcelById(id, showEvents));
    }
}
