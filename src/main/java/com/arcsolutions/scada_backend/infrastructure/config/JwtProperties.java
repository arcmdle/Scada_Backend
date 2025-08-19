// src/main/java/com/arcsolutions/scada_backend/infrastructure/config/JwtProperties.java
package com.arcsolutions.scada_backend.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String secretKey;
    private int expiration;


}
