package com.arcsolutions.scada_backend.infrastructure.mqtt;


import com.arcsolutions.scada_backend.application.mappers.SensorReadingMapper;
import com.arcsolutions.scada_backend.domain.services.ControlService;
import com.arcsolutions.scada_backend.domain.services.TankLevelService;
import com.arcsolutions.scada_backend.infrastructure.dtos.SensorReadingMqtt;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SensorReadingHandler {

    private final TankLevelService tankLevelService;
    private final ControlService controlService;

    public SensorReadingHandler(TankLevelService tankLevelService, ControlService controlService) {
        this.tankLevelService = tankLevelService;
        this.controlService = controlService;
    }

    @ServiceActivator(inputChannel = "sensorReadingChannel")
    public void handleSensorReading(Message<?> message) {
        try {
            Object topicObj = message.getHeaders().get("mqtt_receivedTopic");
            if (!(topicObj instanceof String topic)) {
                System.out.println("⚠️ Topic no válido o ausente");
                return;
            }

            if (!"devices/sensor1/reading".equals(topic)) {
                System.out.println("⚠️ Topic desconocido: " + topic);
                return;
            }

            Object payloadObj = message.getPayload();
            if (!(payloadObj instanceof String payload)) {
                System.out.println("⚠️ Payload no es un String válido");
                return;
            }

            String sensorId = topic.split("/")[1];
            SensorReadingMqtt reading = SensorReadingMapper.fromJson(payload, sensorId);
            System.out.println("📡 Lectura recibida: " + reading);

            tankLevelService.processingSensorReading(reading);

            try {
                controlService.control();
            } catch (RuntimeException ex) {
                System.out.println("⚠️ Control ignorado: " + ex.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Error al procesar lectura MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

