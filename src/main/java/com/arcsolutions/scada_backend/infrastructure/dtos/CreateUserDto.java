package com.arcsolutions.scada_backend.infrastructure.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        @NotBlank
        String fullName,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password,
        String phone,
        String description
) {
}
