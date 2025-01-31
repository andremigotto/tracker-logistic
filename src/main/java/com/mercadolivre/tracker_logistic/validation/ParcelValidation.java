package com.mercadolivre.tracker_logistic.validation;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class ParcelValidation {

    public static ParcelEntity validateParcelExists(ParcelRepository parcelRepository, UUID parcelId) {
        return parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found by ID"));
    }
}
