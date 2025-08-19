package com.arcsolutions.scada_backend.application;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth.cookie")
public class AuthCookieProperties {
    private String tokenName;
    private String path;
    private String domain;
    private boolean httpOnly;
    private boolean secure;
    private int maxAge;
    private String sameSite;
}