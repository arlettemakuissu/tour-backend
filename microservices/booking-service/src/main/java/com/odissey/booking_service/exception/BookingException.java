package com.odissey.booking_service.exception;

public class BookingException extends RuntimeException {

    public BookingException(String message) {
        super(message);
    }

    public String message(){
        return getMessage();
    }
}
