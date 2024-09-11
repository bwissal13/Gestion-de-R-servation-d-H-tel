package com.hotel.model;

import com.hotel.enums.BookingStatus;

import java.time.LocalDate;

public class VipReservation extends Reservation {
    private String specialRequest;

    public VipReservation(int id, Customer customer, Room room, LocalDate checkInDate, LocalDate checkOutDate, BookingStatus status, String specialRequest) {
        super(id, customer, room, checkInDate, checkOutDate, status);
        this.specialRequest = specialRequest;
    }


    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    @Override
    public double calculateTotalPrice() {
        double basePrice = super.calculateTotalPrice();
        return basePrice * 1.2; // 20% de supplément pour VIP
    }

    // toString
    @Override
    public String toString() {
        return "VipReservation{" +
                "specialRequest='" + specialRequest + '\'' +
                ", " + super.toString() +
                '}';
    }
}
