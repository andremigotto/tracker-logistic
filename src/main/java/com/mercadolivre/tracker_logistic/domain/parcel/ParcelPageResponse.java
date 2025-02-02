package com.mercadolivre.tracker_logistic.domain.parcel;

import java.util.List;

public record ParcelPageResponse(

        int pageNumber,
        int totalPages,
        long totalElements,
        List<ParcelEntity> parcels
) {
    public boolean isEmpty() {
        return parcels == null || parcels.isEmpty();
    }
}


