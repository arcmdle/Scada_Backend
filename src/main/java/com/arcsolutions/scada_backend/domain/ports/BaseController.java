package com.arcsolutions.scada_backend.domain.ports;

public interface BaseController {
    boolean isOn();


    void turnOn();

    void turnOff();

    void updateStatus(boolean isOn);
}
