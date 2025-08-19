package com.arcsolutions.scada_backend.infrastructure;

import com.arcsolutions.scada_backend.domain.ports.PumpController;
import com.arcsolutions.scada_backend.infrastructure.mqtt.MqttCommandGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PumpControllerImpl implements PumpController {
    private final MqttCommandGateway gateway;
    private static final String COMMAND_TOPIC = "devices/pump/command";
    private boolean isOn;

    public PumpControllerImpl(MqttCommandGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public boolean isOn() {
        return this.isOn;
    }

    @Override
    public void turnOn() {
        if (!isOn) {
            gateway.sendPumpCommand(COMMAND_TOPIC, "ON");
            isOn = true;
        } else {
            log.debug("⚠️ La bomba ya está encendida. No se requiere acción.");
        }
    }

    @Override
    public void turnOff() {
        if (isOn) {
            gateway.sendPumpCommand(COMMAND_TOPIC, "OFF");
            isOn = false;
        } else {
            log.debug("⚠️ La bomba ya está apagada. No se requiere acción.");
        }
    }

    @Override
    public void updateStatus(boolean isOn) {
        this.isOn = isOn;
    }
}
