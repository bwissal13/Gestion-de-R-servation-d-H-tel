package com.hotel;

import com.hotel.enums.BookingStatus;
import com.hotel.enums.RoomType;
import com.hotel.exceptions.ReservationNotFoundException;
import com.hotel.model.Customer;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.service.CustomerService;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import com.hotel.utils.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final CustomerService customerService = new CustomerService();
    private static final RoomService roomService = new RoomService();
    private static final ReservationService reservationService = new ReservationService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeRooms(); // Initialisation des chambres
        Customer currentCustomer = authenticate();
        if (currentCustomer == null) {
            System.out.println("Échec de l'authentification. Fin du programme.");
            return;
        }

        boolean exit = false;
        while (!exit) {
            showMenu();
            int choice = readInt("Choisissez une option : ");

            switch (choice) {
                case 1:
                    createReservation();
                    break;
                case 2:
                    modifyReservation();
                    break;
                case 3:
                    cancelReservation();
                    break;
                case 4:
                    displayReservationDetails();
                    break;
                case 5:
                    displayStatistics();
                    break;
                case 6:
                    exit = true;
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- Système de Gestion des Réservations d'Hôtel ---");
        System.out.println("1. Créer une Réservation");
        System.out.println("2. Modifier une Réservation");
        System.out.println("3. Annuler une Réservation");
        System.out.println("4. Afficher les Détails d'une Réservation");
        System.out.println("5. Afficher les Statistiques");
        System.out.println("6. Quitter");
    }
    private static Customer authenticate() {
        System.out.println("\n--- Authentification du Client ---");
        String email = readString("Email du client : ");

        CustomerService customerService = new CustomerService();
        Optional<Customer> customerOpt = customerService.authenticate(email);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            System.out.println("Client authentifié : " + customer);

            // Afficher les réservations du client
            List<Reservation> reservations = reservationService.findReservationsByCustomerId(customer.getId());
            if (reservations.isEmpty()) {
                System.out.println("Aucune réservation trouvée pour ce client.");
            } else {
                reservations.forEach(System.out::println);
            }
        } else {
            System.out.println("Client non trouvé avec l'email fourni.");

        }
        return null;
    }

    private static void createReservation() {
        System.out.println("\n--- Créer une Nouvelle Réservation ---");
        int reservationId = readInt("ID de la réservation : ");
        int customerId = readInt("ID du client : ");
        String customerName = readString("Nom du client : ");
        String customerEmail = readString("Email du client : ");
        String customerPhone = readString("Téléphone du client : ");

        Customer customer = new Customer(customerId, customerName, customerEmail, customerPhone);

        int roomId = readInt("ID de la chambre : ");
        Optional<Room> roomOpt = roomService.getById(roomId);
        if (!roomOpt.isPresent()) {
            System.out.println("Chambre non trouvée.");
            return;
        }
        Room room = roomOpt.get();

        String checkInStr = readString("Date d'arrivée (YYYY-MM-DD) : ");
        String checkOutStr = readString("Date de départ (YYYY-MM-DD) : ");

        LocalDate checkInDate;
        LocalDate checkOutDate;

        try {
            checkInDate = LocalDate.parse(checkInStr);
            checkOutDate = LocalDate.parse(checkOutStr);
        } catch (Exception e) {
            System.out.println("Format de date invalide.");
            return;
        }

        if (!ValidationUtils.isValidDateRange(checkInDate, checkOutDate)) {
            System.out.println("Plage de dates invalide.");
            return;
        }
//
//        // Vérifier la disponibilité de la chambre
//        if (!reservationService.isRoomAvailable(roomId, checkInDate, checkOutDate)) {
//            System.out.println("La chambre n'est pas disponible pour les dates sélectionnées.");
//            return;
//        }

        Reservation reservation = new Reservation(reservationId, customer, room, checkInDate, checkOutDate, BookingStatus.ACTIVE);
        try {
            reservationService.create(reservation);
            System.out.println("Réservation créée avec succès.");
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la réservation : " + e.getMessage());
        }
    }

    private static void modifyReservation() {
        System.out.println("\n--- Modifier une Réservation ---");
        int reservationId = readInt("ID de la réservation à modifier : ");
        Optional<Reservation> reservationOpt = reservationService.getById(reservationId);
        if (!reservationOpt.isPresent()) {
            System.out.println("Réservation non trouvée.");
            return;
        }

        Reservation reservation = reservationOpt.get();
        String newCheckIn = readString("Nouvelle date d'arrivée (YYYY-MM-DD) : ");
        String newCheckOut = readString("Nouvelle date de départ (YYYY-MM-DD) : ");

        try {
            LocalDate newCheckInDate = LocalDate.parse(newCheckIn);
            LocalDate newCheckOutDate = LocalDate.parse(newCheckOut);

            if (!ValidationUtils.isValidDateRange(newCheckInDate, newCheckOutDate)) {
                System.out.println("Plage de dates invalide.");
                return;
            }

            reservation.setCheckInDate(newCheckInDate);
            reservation.setCheckOutDate(newCheckOutDate);
            reservationService.update(reservation);
            System.out.println("Réservation mise à jour avec succès.");
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification de la réservation : " + e.getMessage());
        }
    }

    private static void cancelReservation() {
        System.out.println("\n--- Annuler une Réservation ---");
        int reservationId = readInt("ID de la réservation à annuler : ");
        try {
            reservationService.delete(reservationId);
            System.out.println("Réservation annulée avec succès.");
//        } catch (ReservationNotFoundException e) {
//            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de l'annulation de la réservation : " + e.getMessage());
        }
    }

    private static void displayReservationDetails() {
        System.out.println("\n--- Détails d'une Réservation ---");
        int reservationId = readInt("ID de la réservation : ");
        Optional<Reservation> reservationOpt = reservationService.getById(reservationId);
        if (reservationOpt.isPresent()) {
            System.out.println(reservationOpt.get());
        } else {
            System.out.println("Réservation non trouvée.");
        }
    }

    private static void displayStatistics() {
        System.out.println("\n--- Statistiques ---");
        double occupancyRate = reservationService.calculateOccupancyRate();
        double totalRevenue = reservationService.calculateTotalRevenue();
        long cancelledReservations = reservationService.countCancelledReservations();

        System.out.println("Taux d'occupation : " + String.format("%.2f", occupancyRate) + "%");
        System.out.println("Revenus totaux : " + totalRevenue + "€");
        System.out.println("Nombre de réservations annulées : " + cancelledReservations);
    }

    // Méthodes auxiliaires pour lire les entrées utilisateur
    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Veuillez entrer un nombre valide. " + prompt);
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consommer le saut de ligne
        return value;
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    // Méthode d'initialisation des chambres
    private static void initializeRooms() {
        Room room1 = new Room(1, RoomType.SIMPLE, 100.0, true);
        Room room2 = new Room(2, RoomType.DOUBLE, 150.0, true);
        Room room3 = new Room(3, RoomType.SUITE, 250.0, true);
        Room room4 = new Room(4, RoomType.VIP, 500.0, true);

        roomService.create(room1);
        roomService.create(room2);
        roomService.create(room3);
        roomService.create(room4);

        System.out.println("Chambres initialisées.");
    }
}
