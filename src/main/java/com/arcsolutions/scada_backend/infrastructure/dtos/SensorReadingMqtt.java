package com.arcsolutions.scada_backend.infrastructure.dtos;

import java.time.LocalDateTime;

public record SensorReadingMqtt(
        String sensorId,
        double distance,
        String unit,
        LocalDateTime timestamp

) {
}
