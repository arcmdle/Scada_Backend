package com.arcsolutions.scada_backend.application.mappers;

import com.arcsolutions.scada_backend.infrastructure.dtos.SensorReadingMqtt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

public class SensorReadingMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private SensorReadingMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static SensorReadingMqtt fromJson(String json, String sensorId) {
        try {
            RawReading raw = objectMapper.readValue(json, RawReading.class);

            return new SensorReadingMqtt(
                    sensorId,
                    raw.distance,
                    "cm",
                    Instant.now()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al deserializar lectura del sensor", e);
        }
    }

    // ✅ Clase estática interna
    public static class RawReading {
        public double distance;

        public RawReading() {
        } // Jackson necesita constructor vacío
    }
}
