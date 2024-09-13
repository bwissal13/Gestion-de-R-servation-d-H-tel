package com.hotel.service;

import com.hotel.enums.BookingStatus;
import com.hotel.exceptions.ReservationNotFoundException;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import com.hotel.utils.ValidationUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;

    public ReservationService(ReservationRepository reservationRepository, RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
    }

    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> reservations = reservationRepository.findReservationsByRoomId(roomId);

        for (Reservation reservation : reservations) {
            LocalDate reservedCheckInDate = reservation.getCheckInDate();
            LocalDate reservedCheckOutDate = reservation.getCheckOutDate();

            if (checkInDate.isBefore(reservedCheckOutDate) && checkOutDate.isAfter(reservedCheckInDate)) {
                return false;
            }
        }
        return true;
    }

    public Reservation create(Reservation reservation) {
        if (!ValidationUtils.isValidDateRange(reservation.getCheckInDate(), reservation.getCheckOutDate())) {
            throw new IllegalArgumentException("La plage de dates est invalide.");
        }
        if (isRoomAvailable(
                reservation.getRoom().getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate()
        )) {
            return reservationRepository.save(reservation);

        }
        throw new IllegalStateException("La chambre n'est pas disponible pour les dates sélectionnées.");
    }


    public Optional<Reservation> getById(Integer id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }
    public List<Reservation> findReservationsByCustomerId(int customerId) {
        return reservationRepository.findReservationsByCustomerId(customerId);
    }

    public Reservation update(Reservation reservation) {
        Optional<Reservation> existing = reservationRepository.findById(reservation.getId());
        if (existing.isPresent()) {
            return reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Réservation non trouvée");
        }
    }
    public Optional<Reservation> findById(int id) throws ReservationNotFoundException {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation == null) {
            throw new ReservationNotFoundException("Réservation non trouvée pour l'ID : " + id);
        }
        return reservation;
    }
//    public void delete(Integer id) {
//        Optional<Reservation> existing = reservationRepository.findById(id);
//        if (existing.isPresent()) {
//            reservationRepository.deleteById(id);
//        } else {
//            System.out.println("Réservation avec ID " + id + " non trouvée.");
//        }
//    }

public void updateReservationStatus() {
    List<Reservation> reservations = reservationRepository.getAll();
    LocalDate today = LocalDate.now();

    for (Reservation reservation : reservations) {

        if (reservation.getStatus().isActive() && reservation.getCheckOutDate().isBefore(today)) {
            reservation.setStatus(BookingStatus.COMPLETED);
            reservationRepository.save(reservation);
        }
    }
}
    public double calculateOccupancyRate(){
        List<Reservation> reservations = reservationRepository.findAll();
        long totalRooms = roomService.getAll().size();
        long occupiedRooms = reservations.stream()
                .filter(r -> r.getStatus().isActive())
                .map(Reservation::getRoom)
                .distinct()
                .count();
        return ((double) occupiedRooms / totalRooms) * 100;
    }

    public double calculateTotalRevenue() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .filter(r -> r.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(r -> r.getRoom().getPrice() *
                        (ChronoUnit.DAYS.between(r.getCheckInDate(), r.getCheckOutDate())))
                .sum();
    }

    public long countCancelledReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .filter(r -> r.getStatus() == BookingStatus.CANCELLED)
                .count();

    }

}
