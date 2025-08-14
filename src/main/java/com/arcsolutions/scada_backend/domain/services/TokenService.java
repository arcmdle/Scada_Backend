package com.arcsolutions.scada_backend.domain.services;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateToken(Authentication authentication);

    String getUserFromToken(String token);

    boolean validateToken(String token);
}
