package com.odissey.auth_service.exception;

public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }

    public String message(){
        return getMessage();
    }
}
