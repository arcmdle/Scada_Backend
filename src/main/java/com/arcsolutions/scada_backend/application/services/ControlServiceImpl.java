package com.arcsolutions.scada_backend.application.services;

import com.arcsolutions.scada_backend.domain.ports.PumpController;
import com.arcsolutions.scada_backend.domain.ports.ValveController;
import com.arcsolutions.scada_backend.domain.services.ControlService;
import com.arcsolutions.scada_backend.domain.services.TankLevelService;
import com.arcsolutions.scada_backend.infrastructure.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.arcsolutions.scada_backend.domain.services.TankLevelService.MAX_HEIGHT_CM;

@Slf4j
@Service
@RequiredArgsConstructor
public class ControlServiceImpl implements ControlService {
    private final TankLevelService tankLevelService;
    private final PumpController pumpController;
    private final ValveController valveController;
    private final SimpMessagingTemplate messagingTemplate;

    private boolean autoMode = true;
    private double setpoint = 50.0;
    private double hysteresis = 10.0;

    // Estado anterior para evitar logs repetitivos
    private Boolean lastPumpStatus = false;
    private Boolean lastValveStatus = false;

    public void control() {
        if (!autoMode) return;

        double level = tankLevelService.getCurrentLevel().levelCm();
        Instant now = Instant.now();

        boolean pumpShouldBeOn = false;
        boolean valveShouldBeOn = false;

        if (level < setpoint - hysteresis) {
            pumpShouldBeOn = true;
        } else if (level > setpoint + hysteresis) {
            valveShouldBeOn = true;
        }

        // Solo loguear si hay cambio de estado
        if (pumpShouldBeOn != lastPumpStatus || valveShouldBeOn != lastValveStatus) {
            log.info("Cambio de estado → Bomba: {}, Válvula: {}",
                    pumpShouldBeOn ? "ON" : "OFF",
                    valveShouldBeOn ? "ON" : "OFF");
        }

        // Aplicar cambios físicos
        if (pumpShouldBeOn) {
            pumpController.turnOn();
        } else {
            pumpController.turnOff();
        }

        if (valveShouldBeOn) {
            valveController.turnOn();
        } else {
            valveController.turnOff();
        }

        // Actualizar estado y enviar al dashboard
        updateStatus(pumpShouldBeOn, valveShouldBeOn, now);
        lastPumpStatus = pumpShouldBeOn;
        lastValveStatus = valveShouldBeOn;
    }

    private void updateStatus(boolean pumpStatus, boolean valveStatus, Instant timestamp) {
        String topic = "/topic/devices";
        ControlStatusDto payload = ControlStatusDto.builder()
                .pumpStatus(pumpStatus)
                .valveStatus(valveStatus)
                .timestamp(timestamp)
                .build();
        try {
            messagingTemplate.convertAndSend(topic, payload);
        } catch (MessagingException e) {
            log.error("Error al enviar actualización de estado", e);
        }
    }

    @Override
    public SetpointResDto getSetpoint() {
        return new SetpointResDto(setpoint, Instant.now());
    }

    @Override
    public void updateSetpoint(SetpointReqDto reqDto) {
        if (reqDto.newValue() < 0 || reqDto.newValue() > MAX_HEIGHT_CM) {
            log.warn("Setpoint fuera de rango: {}", reqDto.newValue());
            return;
        }
        if (this.setpoint == reqDto.newValue()) {
            log.debug("Setpoint ya establecido. No se harán cambios.");
            return;
        }

        this.setpoint = reqDto.newValue();
        if (isAutoModeEnabled()) {
            control();
        }

        SetpointResDto response = new SetpointResDto(this.setpoint, Instant.now());
        try {
            messagingTemplate.convertAndSend("/topic/setpoint", response);
            log.info("Setpoint actualizado de {} a {} por {} en {}",
                    reqDto.priorValue(), reqDto.newValue(), reqDto.blame(), reqDto.timestamp());
        } catch (MessagingException e) {
            log.error("Error al enviar la actualización del setpoint al dashboard", e);
        }
    }

    @Override
    public double getHysteresis() {
        return hysteresis;
    }

    @Override
    public void updateHysteresis(double hysteresis) {
        this.hysteresis = hysteresis;
    }

    @Override
    public void enableManualMode() {
        this.autoMode = false;
    }

    @Override
    public void manualValveControl(ManualValveDto dto) {
        if (isAutoModeEnabled()) {
            throw new RuntimeException("Modo manual desactivado.");
        }
        if (dto.newStatus()) {
            valveController.turnOn();
        } else {
            valveController.turnOff();
        }
    }

    @Override
    public void manualPumpControl(ManualPumpDto dto) {
        if (isAutoModeEnabled()) {
            throw new RuntimeException("Modo manual desactivado.");
        }
        if (dto.newStatus()) {
            pumpController.turnOn();
        } else {
            pumpController.turnOff();
        }
    }

    @Override
    public boolean isAutoModeEnabled() {
        return autoMode;
    }
}
