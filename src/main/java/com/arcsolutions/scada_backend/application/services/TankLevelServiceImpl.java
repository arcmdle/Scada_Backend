package com.arcsolutions.scada_backend.application.services;

import com.arcsolutions.scada_backend.application.mappers.DataSensorMapper;
import com.arcsolutions.scada_backend.domain.DataSensor;
import com.arcsolutions.scada_backend.domain.SensorDataRepository;
import com.arcsolutions.scada_backend.domain.services.TankLevelService;
import com.arcsolutions.scada_backend.infrastructure.dtos.CurrentLevelDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.DataSensorDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.SensorReadingMqtt;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TankLevelServiceImpl implements TankLevelService {

    private final SensorDataRepository sensorDataRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public double calculateLevelCm(double distance) {
        if (distance < 0 || distance > MAX_HEIGHT_CM) {
            throw new IllegalArgumentException("Distancia medida fuera de rango... revisar sensor");
        }

        double levelCm = MAX_HEIGHT_CM - distance;

        return Math.round(levelCm * 100.0) / 100.0;

    }

    @Override
    public double calculateLevelPercentage(double level) {
        double percentage = (level / MAX_HEIGHT_CM) * 100;
        return Math.round(percentage * 100.0) / 100.0;

    }

    @Override
    public void processingSensorReading(SensorReadingMqtt readingMqtt) {
        double levelCm = calculateLevelCm(readingMqtt.distance());
        double levelPercentage = calculateLevelPercentage(levelCm);
        DataSensor data = DataSensor.builder()
                .sensorId(readingMqtt.sensorId())
                .distance(readingMqtt.distance())
                .levelCm(levelCm)
                .levelPercentage(levelPercentage)
                .timestamp(readingMqtt.timestamp())
                .build();

        sensorDataRepository.save(data);
        String topic = String.format("/topic/%s", readingMqtt.sensorId());
        try {
            messagingTemplate.convertAndSend(topic, DataSensorMapper.fromEntity(data));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public CurrentLevelDto getCurrentLevel() {

        DataSensor latest = sensorDataRepository.getLatest();
        return new CurrentLevelDto(latest.getLevelCm(), latest.getLevelPercentage());
    }

    @Override
    public List<DataSensorDto> getLevelHistory(LocalDateTime from, LocalDateTime to) {
        List<DataSensor> history = sensorDataRepository.getHistoryPeriod(from, to);
        return DataSensorMapper.historyToDto(history);
    }
}
