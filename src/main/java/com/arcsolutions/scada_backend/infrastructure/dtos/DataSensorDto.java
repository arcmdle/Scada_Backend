package com.arcsolutions.scada_backend.infrastructure.dtos;

import java.time.LocalDateTime;

public record DataSensorDto(
        String sensorId,
        Double levelCm,
        Double levelPercentage,
        LocalDateTime timestamp
) {
}
