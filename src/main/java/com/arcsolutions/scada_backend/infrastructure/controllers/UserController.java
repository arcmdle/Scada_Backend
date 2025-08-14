package com.arcsolutions.scada_backend.infrastructure.controllers;

import com.arcsolutions.scada_backend.application.AuthCookieConstants;
import com.arcsolutions.scada_backend.domain.services.TokenService;
import com.arcsolutions.scada_backend.domain.services.UserService;
import com.arcsolutions.scada_backend.infrastructure.config.ApiConfig;
import com.arcsolutions.scada_backend.infrastructure.dtos.UpdateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/user")
public class UserController {
    private final TokenService tokenService;
    private final UserService userService;

    public UserController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(@CookieValue(name = AuthCookieConstants.TOKEN_COOKIE_NAME) String jwtToken) {
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
