package com.arcsolutions.scada_backend.infrastructure.dtos;

public record ManualControlDto(
        boolean enabled,
        boolean pumpStatus,
        boolean valveStatus

) {
}
