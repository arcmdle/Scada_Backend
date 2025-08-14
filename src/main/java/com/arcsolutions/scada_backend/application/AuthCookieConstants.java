package com.arcsolutions.scada_backend.application;

public class AuthCookieConstants {
    private AuthCookieConstants() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static final String TOKEN_COOKIE_NAME = "auth-token";
    public static final String COOKIE_PATH = "/";
    public static final String COOKIE_DOMAIN = "localhost";
    public static final boolean HTTP_ONLY = true;
    public static final boolean COOKIE_SECURE = false;
    public static final int COOKIE_MAX_AGE = 60 * 60 * 24; // One day
    public static final String SAME_SITE = "None";
}
