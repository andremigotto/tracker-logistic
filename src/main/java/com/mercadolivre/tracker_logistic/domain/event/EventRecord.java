package com.mercadolivre.tracker_logistic.domain.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record EventRecord(
        @NotNull UUID parcelId,
        @NotBlank String location,
        @NotBlank String description,
        @NotNull Instant date
) {
}
