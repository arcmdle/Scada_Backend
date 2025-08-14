package com.arcsolutions.scada_backend.infrastructure.dtos;

public record CurrentLevelDto(
        double levelCm,
        double levelPercentage
) {
}
