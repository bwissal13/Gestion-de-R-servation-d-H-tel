package com.hotel;

import com.hotel.config.DatabaseConfig;
import com.hotel.enums.BookingStatus;
import com.hotel.enums.RoomType;
import com.hotel.exceptions.ReservationNotFoundException;
import com.hotel.model.Customer;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.repository.CustomerRepository;
import com.hotel.repository.ReservationRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.CustomerService;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import com.hotel.utils.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final DatabaseConfig dbConfig = DatabaseConfig.getInstance();
    private static final RoomRepository roomRepository = new RoomRepository(dbConfig);
    private static final ReservationRepository reservationRepository = new ReservationRepository(dbConfig);
    private static final CustomerRepository customerRepository = new CustomerRepository(dbConfig);

    private static final RoomService roomService = new RoomService(roomRepository);
    private static final ReservationService reservationService = new ReservationService(reservationRepository, roomService);
    private static final CustomerService customerService = new CustomerService(customerRepository);

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeRooms();
        reservationService.updateReservationStatus();

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
                    showAllReservations();
                    break;
                case 7:
                    showUsersWithReservations();
                    break;
                case 8:
                    showRoomsNotAvailable();
                    break;
                case 9:
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
        System.out.println("6. Afficher Toutes les Réservations");
        System.out.println("7. Afficher les Utilisateurs et leurs Réservations");
        System.out.println("8. Afficher les Chambres Non Disponibles pour une Période");
        System.out.println("9. Quitter");
    }
    private static void createReservation() {
        System.out.println("\n--- Créer une Nouvelle Réservation ---");

        int customerId = readInt("ID du client : ");
        Optional<Customer> existingCustomer = customerService.getById(customerId);

        Customer customer;
        if (!existingCustomer.isPresent()) {
            String customerName = readString("Nom du client : ");
            String customerEmail = readString("Email du client : ");
            String customerPhone = readString("Téléphone du client : ");

            customer = new Customer(customerId, customerName, customerEmail, customerPhone);
            customerService.create(customer);
            System.out.println("Nouveau client créé.");
        } else {
            customer = existingCustomer.get();
        }

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

        Reservation reservation = new Reservation(customerId, customer, room, checkInDate, checkOutDate, BookingStatus.ACTIVE);
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
        Optional<Reservation> optionalReservation = reservationService.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus(BookingStatus.CANCELLED);
            reservationService.update(reservation);
            System.out.println("Réservation annulée avec succès.");
        } else {
            System.out.println("Réservation non trouvée.");
        }
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
    private static void showAllReservations() {
        System.out.println("\n--- Toutes les Réservations ---");
        List<Reservation> reservations = reservationService.getAll();
        if (reservations.isEmpty()) {
            System.out.println("Aucune réservation trouvée.");
        } else {
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }
        }
    }
    private static void showUsersWithReservations() {
        System.out.println("\n--- Utilisateurs avec Réservations ---");
        List<Customer> customers = customerService.getAll();
        for (Customer customer : customers) {
            System.out.println("Client : " + customer);
            List<Reservation> reservations = reservationService.findReservationsByCustomerId(customer.getId());
            if (reservations.isEmpty()) {
                System.out.println("  Aucune réservation trouvée pour ce client.");
            } else {
                for (Reservation reservation : reservations) {
                    System.out.println("  Réservation : " + reservation);
                }
            }
        }
    }
    private static void showRoomsNotAvailable() {
        System.out.println("\n--- Chambres Non Disponibles pour une Période ---");
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

        List<Room> rooms = roomService.getAll();
        boolean foundUnavailable = false;

        for (Room room : rooms) {
            boolean isAvailable = reservationService.isRoomAvailable(room.getId(), checkInDate, checkOutDate);
            if (!isAvailable) {
                System.out.println("Chambre non disponible : " + room);
                foundUnavailable = true;
            }
        }

        if (!foundUnavailable) {
            System.out.println("Toutes les chambres sont disponibles pour la période donnée.");
        }

        // Pause pour l'utilisateur
        System.out.println("Appuyez sur Entrée pour revenir au menu principal...");
        new Scanner(System.in).nextLine();
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
    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Veuillez entrer un nombre valide. " + prompt);
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }
    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
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
