package com.mercadolivre.tracker_logistic.controller;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelDTO;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.service.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    @Autowired
    private ParcelService parcelService;

    //Responsavel por criar um novo pacote
    @PostMapping
    public ResponseEntity<ParcelEntity> createParcel(@RequestBody ParcelDTO parcelRequest) {
        ParcelEntity parcel = parcelService.createParcel(parcelRequest);
        return ResponseEntity.status(201).body(parcel);
    }

    //Responsavel por consultar um pacote através do seu ID unico.
    @GetMapping("/{parcelId}")
    public ResponseEntity<ParcelEntity> getParcelById(
            @PathVariable("parcelId") UUID id,
            @RequestParam(defaultValue = "true") boolean showEvents) {
        return ResponseEntity.ok(parcelService.getParcelById(id, showEvents));
    }

    //Responsável por atualizar o status de um pacote.
    @PatchMapping("/{parcelId}/status")
    public ResponseEntity<ParcelEntity> updateParcelStatus(
            @PathVariable("parcelId") UUID id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(parcelService.updateParcelStatus(id, request.get("status")));
    }

    @GetMapping
    public ResponseEntity<List<ParcelEntity>> getParcelsByFiter(
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient) {
        return ResponseEntity.ok(parcelService.getParcelsByFilter(sender, recipient));
    }
}
