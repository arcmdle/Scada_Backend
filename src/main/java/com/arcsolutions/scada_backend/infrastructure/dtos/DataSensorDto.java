package com.arcsolutions.scada_backend.infrastructure.dtos;

import java.time.Instant;

public record DataSensorDto(
        String sensorId,
        Double levelCm,
        Double levelPercentage,
        Instant timestamp
) {
}
