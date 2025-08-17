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
    private double setpoint = 50.0; //
    private double hysteresis = 10.0; //


    public void control() {
        if (!autoMode) return;


        double level = tankLevelService.getCurrentLevel().levelCm();

        Instant now = Instant.now();
        if (level < setpoint - hysteresis) {

            pumpController.turnOn();
            valveController.turnOff();
            updateStatus(true, false, now);
            log.info("Nivel bajo → Bomba ON, Válvula OFF");
        } else if (level > setpoint + hysteresis) {
            // Nivel alto → activar válvula, desactivar bomba
            pumpController.turnOff();
            valveController.turnOn();
            updateStatus(false, true, now);
            log.info("Nivel alto → Bomba OFF, Válvula ON");

        } else {
            // Nivel dentro del rango
            pumpController.turnOff();
            valveController.turnOff();
            updateStatus(false, false, now);
            log.info("Nivel estable → Bomba OFF, Válvula OFF");
        }
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
            log.error("Error al enviar actualizacion de estado", e);
        }

    }

    @Override
    public SetpointResDto getSetpoint() {
        return new SetpointResDto(setpoint, Instant.now());
    }

    @Override
    public void updateSetpoint(SetpointReqDto reqDto) {
        // en un futuro los cambios se guardaran en una entidad y se persistiran.
        //validaciones
        if (reqDto.newValue() < 0 || reqDto.newValue() > MAX_HEIGHT_CM) {
            log.error("Setpoint fuera de rango.");
            return;

        }
        if (this.setpoint == reqDto.newValue()) {
            log.error("El setpoint ya fue previamente establecido. No se haran cambios");
            return;
        }

        this.setpoint = reqDto.newValue();
        if (isAutoModeEnabled()) {
            control();
        }
        SetpointResDto response = new SetpointResDto(this.setpoint, Instant.now());

        try {
            String topic = "/topic/setpoint";
            messagingTemplate.convertAndSend(topic, response);
            log.info("Setpoint actualizado de {} a {} por {} en {}",
                    reqDto.priorValue(), reqDto.newValue(), reqDto.blame(), reqDto.timestamp());

        } catch (MessagingException e) {
            log.error("Error al enviar la actualizacion del setpoint al dashboard");
        }

    }

    @Override
    public double getHysteresis() {
        // esto debe pedirse por mqtt
        return hysteresis;
    }

    @Override
    public void enableManualMode() {
        this.autoMode = false;
    }


    @Override
    public void updateHysteresis(double hysteresis) {
        // esto debe enviarse por mqtt

        this.hysteresis = hysteresis;
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
