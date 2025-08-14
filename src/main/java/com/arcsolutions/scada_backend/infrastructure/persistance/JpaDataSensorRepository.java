package com.arcsolutions.scada_backend.infrastructure.persistance;

import com.arcsolutions.scada_backend.domain.DataSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaDataSensorRepository extends JpaRepository<DataSensor, Long> {

    List<DataSensor> findByTimestampBetween(LocalDateTime from, LocalDateTime to);

    DataSensor findTopByOrderByTimestampDesc();
}
