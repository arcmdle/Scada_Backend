package com.arcsolutions.scada_backend.domain.services;

import com.arcsolutions.scada_backend.infrastructure.dtos.ManualPumpDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.ManualValveDto;

public interface ControlService {

    double getSetpoint();

    double getHysteresis();

    void enableManualMode();

    void setSetpoint(double setpoint);

    void setHysteresis(double hysteresis);


    void manualValveControl(ManualValveDto dto);

    void manualPumpControl(ManualPumpDto dto);

    boolean isAutoModeEnabled();

    void control();

}
