package com.arcsolutions.scada_backend.infrastructure.dtos;

import java.time.LocalDateTime;

public record ManualPumpDto(
        String pumpId,
        boolean newStatus,
        LocalDateTime timestamp
) {
}
