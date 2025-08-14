package com.arcsolutions.scada_backend.infrastructure.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttCommandGateway {
    void sendPumpCommand(@Header("mqtt_topic") String topic, String command);

    void sendValveCommand(@Header("mqtt_topic") String topic, String command);

    void sendCommand(@Header("mqtt_topic") String topic, String command);

}
