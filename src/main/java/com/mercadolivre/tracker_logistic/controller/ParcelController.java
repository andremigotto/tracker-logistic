package com.mercadolivre.tracker_logistic.controller;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelPageResponse;
import com.mercadolivre.tracker_logistic.domain.status.StatusEnumeration;
import com.mercadolivre.tracker_logistic.domain.status.StatusRecord;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelRecord;
import com.mercadolivre.tracker_logistic.service.ParcelMaintenenceService;
import com.mercadolivre.tracker_logistic.service.ParcelQueryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    private final ParcelQueryService parcelQueryService;
    private final ParcelMaintenenceService parcelMaintenenceService;

    public ParcelController(ParcelQueryService parcelQueryService, ParcelMaintenenceService parcelMaintenenceService) {
        this.parcelQueryService = parcelQueryService;
        this.parcelMaintenenceService = parcelMaintenenceService;
    }

    //Responsavel por criar um novo pacote
    @PostMapping
    public ResponseEntity<ParcelEntity> createParcel(@Valid @RequestBody ParcelRecord request) {
        ParcelEntity parcel = parcelMaintenenceService.createParcel(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/parcels/" + parcel.getId())
                .header("X-Parcel-ID", parcel.getId().toString())
                .body(parcel);
    }

    //Responsável por atualizar o status de um pacote.
    @PatchMapping("/{parcelId}/status")
    public ResponseEntity<ParcelEntity> updateParcelStatus(@PathVariable("parcelId") UUID id, @Valid @RequestBody StatusRecord status) {

        ParcelEntity parcel = parcelMaintenenceService.updateParcelStatus(id, status);

        return ResponseEntity.ok()
                .header("X-Parcel-ID", parcel.getId().toString())
                .header("X-New-Status", parcel.getStatus())
                .body(parcel);
    }

    //Responsável por cancelar um pacote através do seu ID unico.
    @PatchMapping("/{parcelId}/cancel")
    public ResponseEntity<ParcelEntity> cancelParcel(@PathVariable("parcelId") UUID id) {

        ParcelEntity parcel = parcelMaintenenceService.cancelParcelById(id);

        return ResponseEntity.ok()
                .header("X-Parcel-ID", parcel.getId().toString())
                .header("X-Status", StatusEnumeration.CANCELED.toString())
                .body(parcel);
    }

    //Responsavel por consultar um pacote através do seu ID unico.
    @GetMapping("/{parcelId}")
    public ResponseEntity<ParcelEntity> getParcelById(
            @PathVariable("parcelId") UUID id,
            @RequestParam(defaultValue = "true") boolean showEvents,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
    ) {

        ParcelEntity parcel = parcelQueryService.getParcelById(id, showEvents);
        String eTag = String.valueOf(parcel.hashCode());

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .eTag(eTag)
                .lastModified(parcel.getUpdatedAt().toEpochMilli())
                .header("X-Parcel-ID", parcel.getId().toString())
                .header("X-Parcel-Status", parcel.getStatus())
                .body(parcel);
    }

    //Responsável por consultar pacotes através de filtros
    @GetMapping
    public ResponseEntity<ParcelPageResponse> getParcelsByFilter(
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String recipient,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {

        ParcelPageResponse parcels = parcelQueryService.getParcelsByFilter(sender, recipient, pageable);
        String eTag = String.valueOf(parcels.hashCode());

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .eTag(eTag)
                .lastModified(System.currentTimeMillis())
                .header("X-Total-Elements", String.valueOf(parcels.parcels().size()))
                .header("X-Page-Size", String.valueOf(pageable.getPageSize()))
                .header("X-Current-Page", String.valueOf(pageable.getPageNumber()))
                .body(parcels);
    }
}

