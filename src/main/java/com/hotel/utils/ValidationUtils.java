package com.hotel.utils;

import java.time.LocalDate;

public class ValidationUtils {

    public static boolean isValidDateRange(LocalDate checkIn, LocalDate checkOut) {
        return checkIn != null && checkOut != null && checkIn.isBefore(checkOut);
    }

    public static boolean isEmailValid(String email) {
        // Simple regex pour valider un email
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    // Autres méthodes de validation peuvent être ajoutées ici
}
