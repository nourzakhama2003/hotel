package com.nourproject.hotel.exceptions;

public class InvalidBookingStateOrDate extends RuntimeException{
    public InvalidBookingStateOrDate(String message) {
        super(message);
    }
}
