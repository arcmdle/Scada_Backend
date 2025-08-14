package com.arcsolutions.scada_backend.domain.services;

import com.arcsolutions.scada_backend.domain.User;
import com.arcsolutions.scada_backend.infrastructure.dtos.CreateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.LoginRequestDto;

import java.util.UUID;

public interface AuthService {
    String login(LoginRequestDto loginRequestDto);

    boolean validateToken(String token);

    String getUserFromToken(String token);

    void createUser(CreateUserDto createUserDto);

    User getUser(UUID id);
}
