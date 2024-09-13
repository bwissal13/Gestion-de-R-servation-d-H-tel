package com.hotel.repository;

import com.hotel.config.DatabaseConfig;
import com.hotel.enums.BookingStatus;
import com.hotel.enums.RoomType;
import com.hotel.model.Customer;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public class ReservationRepository {

    private final DatabaseConfig databaseConfig;

    // Constructor to inject DatabaseConfig
    public ReservationRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservations (id, customer_id, room_id, check_in_date, check_out_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET customer_id = ?, room_id = ?, check_in_date = ?, check_out_date = ?, status = ?";
        try (Connection conn = databaseConfig.getConnection();
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
        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

    public List<Reservation> findByCustomerId(int customerId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE customer_id = ?";
        try (Connection conn = databaseConfig.getConnection();
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

    public Optional<Reservation> findById(Integer id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
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

    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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


//    public void deleteById(Integer id) {
//        String sql = "DELETE FROM reservations WHERE id = ?";
//        try (Connection conn = databaseConfig.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, id);
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    // Helper methods to fetch Customer and Room from database
    private Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
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
        return null;
    }

    private Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        RoomType.valueOf(rs.getString("type")),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Reservation> findReservationsByCustomerId(int customerId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE customer_id = ?";
        try (Connection conn = databaseConfig.getConnection();
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
    public List<Reservation> findReservationsByRoomId(int roomId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE room_id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
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
}
