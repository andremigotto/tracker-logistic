package com.mercadolivre.tracker_logistic.domain.parcel;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ParcelRecord(
        @NotBlank String description,
        String funFact,
        @NotBlank String sender,
        @NotBlank String recipient,
        Boolean isHolliday,
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "America/Sao_Paulo") LocalDate estimatedDeliveryDate
) {
}
