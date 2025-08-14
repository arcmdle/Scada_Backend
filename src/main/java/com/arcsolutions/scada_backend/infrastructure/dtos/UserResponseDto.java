package com.arcsolutions.scada_backend.infrastructure.dtos;

import com.arcsolutions.scada_backend.domain.Role;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String fullName,
        String email,
        Role role,
        String phone,
        String description,
        String avatarUrl

) {
}
