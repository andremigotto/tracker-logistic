package com.mercadolivre.tracker_logistic.domain.event;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record EventRecord(
        @NotBlank UUID parcelId,
        @NotBlank String location,
        @NotBlank String description,
        @NotBlank Instant date
) {
}
