package com.arcsolutions.scada_backend.application.services;

import com.arcsolutions.scada_backend.application.AuthException;
import com.arcsolutions.scada_backend.application.mappers.UserMapper;
import com.arcsolutions.scada_backend.domain.User;
import com.arcsolutions.scada_backend.domain.UserRepository;
import com.arcsolutions.scada_backend.domain.services.AuthService;
import com.arcsolutions.scada_backend.domain.services.TokenService;
import com.arcsolutions.scada_backend.infrastructure.dtos.CreateUserDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Override
    public void createUser(final CreateUserDto createUserDto) {
        String encodedPassword = passwordEncoder.encode(createUserDto.password());
        final User createUser = UserMapper.fromDto(createUserDto, encodedPassword);
        final User user = userRepository.save(createUser);
        log.info("[USER] Usuario creado exitosamente con ID {}", user.getId());
    }

    @Override
    public User getUser(final UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[USER] Usuario no encontrado con ID {}", id);
                    return new AuthException("User Not Found");
                });
    }

    @Override
    public String login(final LoginRequestDto loginRequest) {
        try {
            log.debug("Intento de login con email: {}", loginRequest.email());

            UserDetails userDetails = loadUserByUsername(loginRequest.email());
            log.debug("Password en base de datos: {}", userDetails.getPassword());

            final AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            final Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password()
            );

            final Authentication authentication = authenticationManager.authenticate(authRequest);
            return tokenService.generateToken(authentication);

        } catch (Exception e) {
            log.error("[USER] Error durante el login", e);
            throw new ProviderNotFoundException("Error while trying to login");
        }
    }

    @Override
    public boolean validateToken(final String token) {
        return tokenService.validateToken(token);
    }

    @Override
    public String getUserFromToken(final String token) {
        return tokenService.getUserFromToken(token);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("[USER] Usuario no encontrado con email {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of()
        );
    }
}
