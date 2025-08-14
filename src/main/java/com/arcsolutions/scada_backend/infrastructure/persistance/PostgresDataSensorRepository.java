package com.arcsolutions.scada_backend.infrastructure.persistance;

import com.arcsolutions.scada_backend.domain.DataSensor;
import com.arcsolutions.scada_backend.domain.SensorDataRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PostgresDataSensorRepository implements SensorDataRepository {
    private final JpaDataSensorRepository jpaDataSensorRepository;

    public PostgresDataSensorRepository(JpaDataSensorRepository jpaDataSensorRepository) {
        this.jpaDataSensorRepository = jpaDataSensorRepository;
    }

    @Override
    public DataSensor save(DataSensor data) {
        return jpaDataSensorRepository.save(data);
    }

    @Override
    public List<DataSensor> getHistoryPeriod(LocalDateTime from, LocalDateTime to) {
        return jpaDataSensorRepository.findByTimestampBetween(from, to);
    }

    @Override
    public DataSensor getLatest() {
        return jpaDataSensorRepository.findTopByOrderByTimestampDesc();
    }
}
