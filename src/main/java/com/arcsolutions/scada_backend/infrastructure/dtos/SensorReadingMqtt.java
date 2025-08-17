package com.arcsolutions.scada_backend.infrastructure.dtos;

import java.time.Instant;

public record SensorReadingMqtt(
        String sensorId,
        double distance,
        String unit,
        Instant timestamp

) {
}
