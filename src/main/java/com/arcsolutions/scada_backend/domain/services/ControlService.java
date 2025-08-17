package com.arcsolutions.scada_backend.domain.services;

import com.arcsolutions.scada_backend.infrastructure.dtos.ManualPumpDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.ManualValveDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.SetpointReqDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.SetpointResDto;

public interface ControlService {

    SetpointResDto getSetpoint();

    void updateSetpoint(SetpointReqDto reqDto);

    double getHysteresis();

    void enableManualMode();


    void updateHysteresis(double hysteresis);


    void manualValveControl(ManualValveDto dto);

    void manualPumpControl(ManualPumpDto dto);

    boolean isAutoModeEnabled();

    void control();

}
