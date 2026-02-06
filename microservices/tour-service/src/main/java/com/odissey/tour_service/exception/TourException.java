package com.odissey.tour_service.exception;

public class TourException extends RuntimeException {

    public TourException(String message) {
        super(message);
    }

    public String message(){
        return getMessage();
    }
}
