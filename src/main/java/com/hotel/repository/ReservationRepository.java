package com.hotel.repository;

import com.hotel.config.DatabaseConfig;
import com.hotel.enums.RoomType;
import com.hotel.model.Customer;
import com.hotel.model.Reservation;
import com.hotel.enums.BookingStatus;
import com.hotel.model.Room;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepository implements Repository<Reservation, Integer> {

    private static ReservationRepository instance;

    private ReservationRepository() {

    }

    public static synchronized ReservationRepository getInstance() {
        if (instance == null) {
            instance = new ReservationRepository();
        }
        return instance;
    }

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservations (id, customer_id, room_id, check_in_date, check_out_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET customer_id = ?, room_id = ?, check_in_date = ?, check_out_date = ?, status = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Insert
            pstmt.setInt(1, reservation.getId());
            pstmt.setInt(2, reservation.getCustomer().getId());
            pstmt.setInt(3, reservation.getRoom().getId());
            pstmt.setDate(4, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(5, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(6, reservation.getStatus().name());

            // Update
            pstmt.setInt(7, reservation.getCustomer().getId());
            pstmt.setInt(8, reservation.getRoom().getId());
            pstmt.setDate(9, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(10, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(11, reservation.getStatus().name());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservation;
    }
    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("id"),
                        getCustomerById(rs.getInt("customer_id")), // Assurez-vous d'implémenter cette méthode
                        getRoomById(rs.getInt("room_id")),         // Assurez-vous d'implémenter cette méthode
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        BookingStatus.valueOf(rs.getString("status"))
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public List<Reservation> findByCustomerId(int customerId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE customer_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("id"),
                        getCustomerById(rs.getInt("customer_id")),
                        getRoomById(rs.getInt("room_id")),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        BookingStatus.valueOf(rs.getString("status"))
                );
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
    @Override
    public Optional<Reservation> findById(Integer id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("id"),
                        getCustomerById(rs.getInt("customer_id")),
                        getRoomById(rs.getInt("room_id")),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        BookingStatus.valueOf(rs.getString("status"))
                );
                return Optional.of(reservation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("id"),
                        getCustomerById(rs.getInt("customer_id")), // Méthode à implémenter
                        getRoomById(rs.getInt("room_id")),         // Méthode à implémenter
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        BookingStatus.valueOf(rs.getString("status"))
                );
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Ou lancer une exception personnalisée si nécessaire
    }
    private Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        RoomType.valueOf(rs.getString("type")), // Assurez-vous que RoomType est bien géré
                        rs.getDouble("price"),
                        rs.getBoolean("is_available")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Ou lancer une exception personnalisée si nécessaire
    }

}
