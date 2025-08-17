package com.arcsolutions.scada_backend.infrastructure.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record SetpointReqDto(

        Double priorValue,
        @Positive
        @NotNull
        Double newValue,
        Instant timestamp,
        String blame
) {
}
