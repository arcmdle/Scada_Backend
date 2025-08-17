package com.arcsolutions.scada_backend.infrastructure.dtos;

import java.time.Instant;

public record SetpointResDto(
        Double setpoint,
        Instant timestamp
) {
}
