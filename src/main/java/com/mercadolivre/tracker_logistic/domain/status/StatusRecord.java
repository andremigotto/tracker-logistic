package com.mercadolivre.tracker_logistic.domain.status;

import jakarta.validation.constraints.NotBlank;

public record StatusRecord(@NotBlank String status) {
}
