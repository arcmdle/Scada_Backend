package com.arcsolutions.scada_backend.infrastructure.controllers;

import com.arcsolutions.scada_backend.application.AuthCookieProperties;
import com.arcsolutions.scada_backend.domain.services.TokenService;
import com.arcsolutions.scada_backend.domain.services.UserService;
import com.arcsolutions.scada_backend.infrastructure.config.ApiConfig;
import com.arcsolutions.scada_backend.infrastructure.dtos.UpdateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.UserResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/user")
public class UserController {
    private final TokenService tokenService;
    private final UserService userService;
    private final AuthCookieProperties authCookieProperties;

    public UserController(TokenService tokenService, UserService userService, AuthCookieProperties authCookieProperties) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.authCookieProperties = authCookieProperties;
    }


    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(HttpServletRequest request) {
        String jwtToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> cookie.getName().equals(authCookieProperties.getTokenName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (jwtToken == null) {
            return ResponseEntity.status(401).build(); // o lanzar una excepción
        }

        String email = tokenService.getUserFromToken(jwtToken);
        UserResponseDto user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserDto dto
    ) {
        return ResponseEntity.ok(userService.update(dto, id));
    }


}
