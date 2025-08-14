package com.arcsolutions.scada_backend.application;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
