package com.arcsolutions.scada_backend.infrastructure.mqtt;

import com.arcsolutions.scada_backend.domain.ports.PumpController;
import com.arcsolutions.scada_backend.domain.ports.ValveController;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MqttStatusHandler {
    private final PumpController pumpController;
    private final ValveController valveController;

    public MqttStatusHandler(PumpController pumpController, ValveController valveController) {
        this.pumpController = pumpController;
        this.valveController = valveController;
    }

    @ServiceActivator(inputChannel = "statusChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        String payload = (String) message.getPayload();

        switch (topic) {
            case "devices/pump/status":
                handlePumpStatus(payload);
                break;
            case "devices/valve/status":
                handleValveStatus(payload);
                break;
            default:
                System.out.println("Topic desconocido: " + topic);
        }
    }

    private void handlePumpStatus(String payload) {
        boolean isOn = "ON".equalsIgnoreCase(payload);
        pumpController.updateStatus(isOn);
        System.out.println("Estado actualizado de la bomba: " + (isOn ? "Encendida" : "Apagada"));
    }

    private void handleValveStatus(String payload) {
        boolean isOpen = "ON".equalsIgnoreCase(payload);
        valveController.updateStatus(isOpen);
        System.out.println("Estado actualizado de la válvula: " + (isOpen ? "Abierta" : "Cerrada"));
    }
}
