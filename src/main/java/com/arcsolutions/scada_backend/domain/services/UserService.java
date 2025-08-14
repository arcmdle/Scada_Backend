package com.arcsolutions.scada_backend.domain.services;

import com.arcsolutions.scada_backend.infrastructure.dtos.UpdateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.UserResponseDto;

import java.util.UUID;

public interface UserService {
    UserResponseDto findById(UUID id);

    UserResponseDto findByEmail(String email);

    UserResponseDto update(UpdateUserDto dto, UUID id);

    void changeAvatar(String avatarUrl, UUID id);

}
