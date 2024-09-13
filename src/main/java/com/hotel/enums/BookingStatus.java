package com.hotel.enums;

public enum BookingStatus {
    ACTIVE,
    CANCELLED,
    COMPLETED;


    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }
}
