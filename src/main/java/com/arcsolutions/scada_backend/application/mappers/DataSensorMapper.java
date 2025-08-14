package com.arcsolutions.scada_backend.application.mappers;

import com.arcsolutions.scada_backend.domain.DataSensor;
import com.arcsolutions.scada_backend.infrastructure.dtos.DataSensorDto;

import java.util.List;
import java.util.stream.Collectors;

public class DataSensorMapper {
    private DataSensorMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static DataSensor fromDto(DataSensorDto dto) {
        return DataSensor.builder()
                .sensorId(dto.sensorId())
                .levelCm(dto.levelCm())
                .levelPercentage(dto.levelPercentage())
                .timestamp(dto.timestamp())
                .build();
    }

    public static DataSensorDto fromEntity(DataSensor entity) {
        return new DataSensorDto(entity.getSensorId(), entity.getLevelCm(), entity.getLevelPercentage(), entity.getTimestamp());
    }

    public static List<DataSensorDto> historyToDto(List<DataSensor> history) {
        return history.stream()
                .map(DataSensorMapper::fromEntity)
                .collect(Collectors.toList());
    }
}
