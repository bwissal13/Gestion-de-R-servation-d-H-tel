package com.hotel.exceptions;

public class RoomUnavailableException extends Exception {
    public RoomUnavailableException(String message) {
        super(message);
    }
}
