package com.mercadolivre.tracker_logistic.domain.parcel;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record ParcelRecord(
        @NotBlank @Size(max = 255) String description,
        @NotBlank @Size(max = 500) String funFact,
        @NotBlank @Size(max = 255) String sender,
        @NotBlank @Size(max = 255) String recipient,
        boolean isHolliday,
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") LocalDate estimatedDeliveryDate) {
}
