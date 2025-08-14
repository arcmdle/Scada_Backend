package com.arcsolutions.scada_backend.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataRepository {
    DataSensor save(DataSensor data);

    List<DataSensor> getHistoryPeriod(LocalDateTime from, LocalDateTime to);

    DataSensor getLatest();
}
