package com.arcsolutions.scada_backend.infrastructure.config;

import com.arcsolutions.scada_backend.application.AuthCookieConstants;
import com.arcsolutions.scada_backend.domain.services.AuthService;
import com.arcsolutions.scada_backend.infrastructure.filters.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity()
public class SecurityConfig {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(AuthService authService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public static final String REGISTER_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/register";
    public static final String LOGIN_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/login";
    public static final String LOG_OUT_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/logout";
    public static final String BASE_URL_MATCHER = ApiConfig.API_BASE_PATH + "/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        Filter jwtFilter = jwtAuthenticationFilter();

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, LOGIN_URL_MATCHER).permitAll()
                        .requestMatchers(HttpMethod.POST, REGISTER_URL_MATCHER).permitAll()
                        .requestMatchers(BASE_URL_MATCHER).authenticated()
                        .anyRequest().permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(LOG_OUT_URL_MATCHER)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.NO_CONTENT.value());
                            Cookie cookie = new Cookie(AuthCookieConstants.TOKEN_COOKIE_NAME, null);
                            cookie.setPath(AuthCookieConstants.COOKIE_PATH);
                            cookie.setDomain(AuthCookieConstants.COOKIE_DOMAIN);
                            cookie.setHttpOnly(AuthCookieConstants.HTTP_ONLY);
                            cookie.setMaxAge(0);
                            cookie.setSecure(AuthCookieConstants.COOKIE_SECURE);
                            cookie.setAttribute("SAME_SITE_KEY", AuthCookieConstants.SAME_SITE);


                            response.addCookie(cookie);
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager())
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint((request, response, authException) ->
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // 👈 tu frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true); // si usas cookies o sesiones

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authService, userDetailsService);
    }
}
