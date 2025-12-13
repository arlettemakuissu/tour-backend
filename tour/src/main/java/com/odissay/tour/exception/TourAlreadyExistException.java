package com.odissay.tour.exception;

public class TourAlreadyExistException  extends Exception409{

    public TourAlreadyExistException(String errMsg) {
        super(errMsg);

    }
}
