package com.arcsolutions.scada_backend.infrastructure.dtos;

import java.time.Instant;

public record ManualPumpDto(
        String pumpId,
        boolean newStatus,
        Instant timestamp
) {
}
