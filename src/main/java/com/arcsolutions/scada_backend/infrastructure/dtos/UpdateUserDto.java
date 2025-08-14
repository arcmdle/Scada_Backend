package com.arcsolutions.scada_backend.infrastructure.dtos;

public record UpdateUserDto(
        String fullName,
        String description,
        String phone
) {
}
