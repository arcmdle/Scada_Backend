package com.arcsolutions.scada_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "data_sensor")
public class DataSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sensorId;
    private Double distance;
    private Double levelCm;
    private Double levelPercentage;
    private Instant timestamp;
}
