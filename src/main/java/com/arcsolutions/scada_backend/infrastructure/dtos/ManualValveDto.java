package com.arcsolutions.scada_backend.infrastructure.dtos;

import java.time.LocalDateTime;

public record ManualValveDto(

        String valveId,
        boolean newStatus,
        LocalDateTime timestamp
) {
}
