package com.arcsolutions.scada_backend.infrastructure;

import com.arcsolutions.scada_backend.domain.ports.ValveController;
import com.arcsolutions.scada_backend.infrastructure.mqtt.MqttCommandGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValveControllerImpl implements ValveController {
    private final static String COMMAND_TOPIC = "devices/valve/command";
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
        }
    }

    @Override
    public void turnOff() {
        if (isOn()) {
            gateway.sendValveCommand(COMMAND_TOPIC, "OFF");
            isOn = false;
        }
    }

    @Override
    public void updateStatus(boolean isOn) {
        this.isOn = isOn;
    }

}
