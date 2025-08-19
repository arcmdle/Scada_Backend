package com.arcsolutions.scada_backend.infrastructure.controllers;

import com.arcsolutions.scada_backend.application.AuthCookieProperties;
import com.arcsolutions.scada_backend.domain.services.AuthService;
import com.arcsolutions.scada_backend.infrastructure.config.ApiConfig;
import com.arcsolutions.scada_backend.infrastructure.dtos.CreateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.LoginRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones de autenticación de usuarios.
 * Expone endpoints para crear usuarios, iniciar sesión y cerrar sesión.
 */
@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieProperties authCookieProperties;

    /**
     * Constructor que inyecta el servicio de autenticación.
     *
     * @param authService Servicio que maneja la lógica de autenticación.
     */
    public AuthController(AuthService authService, AuthCookieProperties authCookieProperties) {
        this.authService = authService;
        this.authCookieProperties = authCookieProperties;
    }

    @PostMapping("/register")

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param createUserDto DTO con los datos del nuevo usuario.
     */
    public void createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        authService.createUser(createUserDto);
    }

    /**
     * Endpoint para iniciar sesión.
     * Genera un token de autenticación y lo envía como cookie en la respuesta.
     *
     * @param loginRequestDto DTO con las credenciales del usuario.
     * @param response        Objeto HttpServletResponse para añadir la cookie.
     */
    @PostMapping("/login")
    public void login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        final String token = authService.login(loginRequestDto);
        response.addCookie(createAuthCookie(token));
    }

    /**
     * Endpoint para cerrar sesión.
     * Elimina la cookie de autenticación.
     *
     * @param response Objeto HttpServletResponse para eliminar la cookie.
     */
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        final Cookie cookie = new Cookie(authCookieProperties.getTokenName(), "");
        cookie.setHttpOnly(authCookieProperties.isHttpOnly());
        cookie.setSecure(authCookieProperties.isSecure());
        cookie.setPath(authCookieProperties.getPath());
        cookie.setMaxAge(0); // Elimina la cookie
        cookie.setAttribute("SameSite", authCookieProperties.getSameSite());
        response.addCookie(cookie);
    }


    /**
     * Crea una cookie de autenticación con los atributos definidos en AuthCookieConstants.
     *
     * @param token Token JWT generado tras el login.
     * @return Cookie configurada para autenticación.
     */
    private Cookie createAuthCookie(String token) {
        final Cookie cookie = new Cookie(authCookieProperties.getTokenName(), token);
        cookie.setHttpOnly(authCookieProperties.isHttpOnly());
        cookie.setSecure(authCookieProperties.isSecure());
        cookie.setPath(authCookieProperties.getPath());
        cookie.setMaxAge(authCookieProperties.getMaxAge());
        cookie.setAttribute("SameSite", authCookieProperties.getSameSite());
        return cookie;
    }

}
