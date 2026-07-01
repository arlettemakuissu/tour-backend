package com.odissey.agency_service.exception;

public class AgencyException extends RuntimeException {

    public AgencyException(String message) {
        super(message);
    }

    public String message(){
        return getMessage();
    }
}
