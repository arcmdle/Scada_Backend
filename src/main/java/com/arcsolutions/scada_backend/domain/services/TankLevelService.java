package com.arcsolutions.scada_backend.domain.services;

import com.arcsolutions.scada_backend.infrastructure.dtos.CurrentLevelDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.DataSensorDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.SensorReadingMqtt;

import java.time.LocalDateTime;
import java.util.List;

public interface TankLevelService {
    static double MAX_HEIGHT_CM = 200.0; // H-max known from the tank.

    double calculateLevelCm(double distance);

    double calculateLevelPercentage(double level);

    void processingSensorReading(SensorReadingMqtt readingMqtt);

    CurrentLevelDto getCurrentLevel();

    List<DataSensorDto> getLevelHistory(LocalDateTime from, LocalDateTime to);


}
