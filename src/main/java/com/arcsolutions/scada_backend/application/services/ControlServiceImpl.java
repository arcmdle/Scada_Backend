package com.arcsolutions.scada_backend.application.services;

import com.arcsolutions.scada_backend.domain.ports.PumpController;
import com.arcsolutions.scada_backend.domain.ports.ValveController;
import com.arcsolutions.scada_backend.domain.services.ControlService;
import com.arcsolutions.scada_backend.domain.services.TankLevelService;
import com.arcsolutions.scada_backend.infrastructure.dtos.ManualPumpDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.ManualValveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ControlServiceImpl implements ControlService {
    private final TankLevelService tankLevelService;
    private final PumpController pumpController;
    private final ValveController valveController;


    private boolean autoMode = true;
    private double setpoint = 50.0; //
    private double hysteresis = 10.0; //


    public void control() {
        if (!autoMode) return;


        double level = tankLevelService.getCurrentLevel().levelCm();

        if (level < setpoint - hysteresis) {

            pumpController.turnOn();
            valveController.turnOff();
            System.out.println("Nivel bajo → Bomba ON, Válvula OFF");
        } else if (level > setpoint + hysteresis) {
            // Nivel alto → activar válvula, desactivar bomba
            pumpController.turnOff();
            valveController.turnOn();
            System.out.println("Nivel alto → Bomba OFF, Válvula ON");
        } else {
            // Nivel dentro del rango
            pumpController.turnOff();
            valveController.turnOff();
            System.out.println("Nivel estable → Bomba OFF, Válvula OFF");
        }
    }

    @Override
    public double getSetpoint() {
        // esto debe pedirse por mqtt

        return setpoint;
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
    public void setSetpoint(double setpoint) {
        // esto debe enviarse por mqtt
        this.setpoint = setpoint;
    }

    @Override
    public void setHysteresis(double hysteresis) {
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
