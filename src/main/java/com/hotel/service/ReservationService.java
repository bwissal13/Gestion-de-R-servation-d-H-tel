package com.hotel.service;

import com.hotel.enums.BookingStatus;
import com.hotel.exceptions.ReservationNotFoundException;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import com.hotel.utils.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationService implements Service<Reservation, Integer> {

    private final ReservationRepository reservationRepository = ReservationRepository.getInstance();
    private final RoomService roomService = new RoomService();

    @Override
    public Reservation create(Reservation reservation) {
        // Validation des dates
        if (!ValidationUtils.isValidDateRange(reservation.getCheckInDate(), reservation.getCheckOutDate())) {
            throw new IllegalArgumentException("La plage de dates est invalide.");
        }

//        // Vérifier la disponibilité de la chambre
//        if (!roomService.isRoomAvailable(reservation.getRoom().getId(),
//                reservation.getCheckInDate().toString(),
//                reservation.getCheckOutDate().toString())) {
//            throw new IllegalStateException("La chambre n'est pas disponible pour les dates sélectionnées.");
//        }

        // Calculer le prix total
        double totalPrice = reservation.calculateTotalPrice();
        System.out.println("Prix total de la réservation : " + totalPrice + "€");

        // Sauvegarder la réservation
        return reservationRepository.save(reservation);
    }
    public List<Reservation> findReservationsByCustomerId(int customerId) {
        return reservationRepository.findByCustomerId(customerId);
    }
    @Override
    public Optional<Reservation> getById(Integer id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation update(Reservation reservation) {
        Optional<Reservation> existing = reservationRepository.findById(reservation.getId());
        if (existing.isPresent()) {
            // Logique de mise à jour
            return reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Réservation non trouvée");
        }
    }

    @Override
    public void delete(Integer id) {
        Optional<Reservation> existing = reservationRepository.findById(id);
        if (existing.isPresent()) {
            reservationRepository.deleteById(id);
        } else {
//            throw new ReservationNotFoundException("Réservation avec ID " + id + " non trouvée.");
            System.out.println("Réservation avec ID " + id + " non trouvée.");
        }
    }

    // Méthodes pour les rapports statistiques
    public double calculateOccupancyRate() {
        List<Reservation> reservations = reservationRepository.findAll();
        long totalRooms = roomService.getAll().size();
        long occupiedRooms = reservations.stream()
                .filter(r -> r.getStatus() == BookingStatus.ACTIVE)
                .map(Reservation::getRoom)
                .distinct()
                .count();
        return ((double) occupiedRooms / totalRooms) * 100;
    }

    public double calculateTotalRevenue() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .filter(r -> r.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Reservation::calculateTotalPrice)
                .sum();
    }

    public long countCancelledReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .filter(r -> r.getStatus() == BookingStatus.CANCELLED)
                .count();
    }
    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> reservations = reservationRepository.getAll(); // Méthode pour obtenir toutes les réservations

        for (Reservation reservation : reservations) {
            if (reservation.getRoom().getId() == roomId) {
                LocalDate existingCheckIn = reservation.getCheckInDate();
                LocalDate existingCheckOut = reservation.getCheckOutDate();

                // Vérifier si les dates se chevauchent
                if (checkInDate.isBefore(existingCheckOut) && checkOutDate.isAfter(existingCheckIn)) {
                    return false; // La chambre n'est pas disponible
                }
            }
        }
        return true; // La chambre est disponible
    }
}
