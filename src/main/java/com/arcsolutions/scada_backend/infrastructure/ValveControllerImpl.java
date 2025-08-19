package com.arcsolutions.scada_backend.infrastructure;

import com.arcsolutions.scada_backend.domain.ports.ValveController;
import com.arcsolutions.scada_backend.infrastructure.mqtt.MqttCommandGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValveControllerImpl implements ValveController {
    private static final String COMMAND_TOPIC = "devices/valve/command";
    private final MqttCommandGateway gateway;

    private boolean isOn;

    @Override
    public boolean isOn() {
        return this.isOn;
    }

    @Override
    public void turnOn() {
        if (!isOn()) {
            gateway.sendValveCommand(COMMAND_TOPIC, "ON");
            isOn = true;
        } else {
            log.debug("⚠️ La válvula ya está abierta. No se requiere acción.");
        }
    }

    @Override
    public void turnOff() {
        if (isOn()) {
            gateway.sendValveCommand(COMMAND_TOPIC, "OFF");
            isOn = false;
        } else {
            log.debug("⚠️ La válvula ya está cerrada. No se requiere acción.");
        }
    }

    @Override
    public void updateStatus(boolean isOn) {
        this.isOn = isOn;
    }
}
