package com.mercadolivre.tracker_logistic.controller;

import com.mercadolivre.tracker_logistic.domain.status.StatusRecord;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelRecord;
import com.mercadolivre.tracker_logistic.service.ParcelMaintenenceService;
import com.mercadolivre.tracker_logistic.service.ParcelQueryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    @Autowired
    private ParcelQueryService parcelQueryService;

    @Autowired
    private ParcelMaintenenceService parcelMaintenenceService;

    //Responsavel por criar um novo pacote
    @PostMapping
    public ResponseEntity<ParcelEntity> createParcel(@Valid @RequestBody ParcelRecord request) {
        ParcelEntity parcel = parcelMaintenenceService.createParcel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(parcel);
    }

    //Responsável por atualizar o status de um pacote.
    @PatchMapping("/{parcelId}/status")
    public ResponseEntity<ParcelEntity> updateParcelStatus(
            @PathVariable("parcelId") UUID id,
            @Valid @RequestBody StatusRecord status) {
        return ResponseEntity.ok(parcelMaintenenceService.updateParcelStatus(id, status));
    }

    //Responsável por cancelar um pacote através do seu ID unico.
    @PatchMapping("/{parcelId}/cancel")
    public ResponseEntity<ParcelEntity> cancelParcel(@PathVariable("parcelId") UUID id) {
        return ResponseEntity.ok(parcelMaintenenceService.cancelParcelById(id));
    }

    //Responsavel por consultar um pacote através do seu ID unico.
    @GetMapping("/{parcelId}")
    public ResponseEntity<ParcelEntity> getParcelById(
            @PathVariable("parcelId") UUID id,
            @RequestParam(defaultValue = "true") boolean showEvents) {
        return ResponseEntity.ok(parcelQueryService.getParcelById(id, showEvents));
    }

    //Responsável por consultar pacotes através de filtros
    @GetMapping
    public ResponseEntity<List<ParcelEntity>> getParcelsByFiter(
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient) {
        return ResponseEntity.ok(parcelQueryService.getParcelsByFilter(sender, recipient));
    }
}
