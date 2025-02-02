package com.mercadolivre.tracker_logistic.domain.status;

import jakarta.validation.constraints.NotNull;

public record StatusRecord(@NotNull StatusEnumeration status) {
}
