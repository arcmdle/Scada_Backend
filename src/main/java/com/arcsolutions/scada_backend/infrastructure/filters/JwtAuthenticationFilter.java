package com.arcsolutions.scada_backend.infrastructure.filters;

import com.arcsolutions.scada_backend.application.AuthCookieProperties;
import com.arcsolutions.scada_backend.domain.services.AuthService;
import com.arcsolutions.scada_backend.infrastructure.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final AuthCookieProperties authCookieProperties;

    // Endpoints públicos que no deben pasar por el filtro
    private static final String[] PUBLIC_ENDPOINTS = {
            SecurityConfig.LOGIN_URL_MATCHER,
            SecurityConfig.REGISTER_URL_MATCHER
    };

    public JwtAuthenticationFilter(AuthService authService, UserDetailsService userDetailsService, AuthCookieProperties authCookieProperties) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.authCookieProperties = authCookieProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(PUBLIC_ENDPOINTS).anyMatch(path::equals);
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        Optional<String> token = getJwtFromCookie(request);

        if (token.isEmpty()) {
            logger.warn("No JWT token found in cookies");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authService.validateToken(token.get())) {
            logger.warn("Invalid JWT token: {}", token.get());
            filterChain.doFilter(request, response);
            return;
        }

        String username = authService.getUserFromToken(token.get());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private Optional<String> getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> authCookieProperties.getTokenName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
