package com.arcsolutions.scada_backend.infrastructure.mqtt;

import com.arcsolutions.scada_backend.domain.ports.PumpController;
import com.arcsolutions.scada_backend.domain.ports.ValveController;
import com.arcsolutions.scada_backend.domain.services.ControlService;
import com.arcsolutions.scada_backend.infrastructure.dtos.SetpointResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttStatusHandler {
    private final PumpController pumpController;
    private final ValveController valveController;
    private final ControlService controlService;
    private final MqttCommandGateway mqttCommandGateway;

    public MqttStatusHandler(PumpController pumpController, ValveController valveController, ControlService controlService, MqttCommandGateway mqttCommandGateway) {
        this.pumpController = pumpController;
        this.valveController = valveController;
        this.controlService = controlService;
        this.mqttCommandGateway = mqttCommandGateway;
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
            case "devices/setpoint/request":
                handleSetpointRequest(payload);
            default:
                log.warn("🔍 Topic desconocido recibido: {}", topic);
        }
    }

    private void handlePumpStatus(String payload) {
        boolean isOn = "ON".equalsIgnoreCase(payload);
        pumpController.updateStatus(isOn);
        log.debug("🚰 Estado de la bomba actualizado a: {}", isOn ? "Encendida" : "Apagada");
    }

    private void handleValveStatus(String payload) {
        boolean isOpen = "ON".equalsIgnoreCase(payload);
        valveController.updateStatus(isOpen);
        log.debug("🔧 Estado de la válvula actualizado a: {}", isOpen ? "Abierta" : "Cerrada");
    }

    private void handleSetpointRequest(String payload) {
        try {
            SetpointResDto setpointDto = controlService.getSetpoint();

            mqttCommandGateway.sendCommand("devices/setpoint/response", setpointDto.setpoint().toString());

            log.info("📤 Setpoint enviado a '{}': {}", payload, setpointDto.setpoint());
        } catch (Exception e) {
            log.error("❌ Error al responder solicitud de setpoint", e);
        }
    }
}
