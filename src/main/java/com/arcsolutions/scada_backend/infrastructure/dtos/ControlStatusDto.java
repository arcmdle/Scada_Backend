package com.arcsolutions.scada_backend.infrastructure.dtos;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ControlStatusDto {
    private Instant timestamp;
    private boolean pumpStatus;
    private boolean valveStatus;

}
