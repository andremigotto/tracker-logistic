package com.mercadolivre.tracker_logistic.domain.status;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum StatusEnumeration {
    CREATED, IN_TRANSIT, DELIVERED, CANCELED;

    @JsonCreator
    public static StatusEnumeration fromString(String value) {
        return Arrays.stream(StatusEnumeration.values())
                .filter(s -> s.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + value));
    }
}


