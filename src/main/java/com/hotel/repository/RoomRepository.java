package com.hotel.repository;

import com.hotel.model.Room;
import com.hotel.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepository implements Repository<Room, Integer> {

    private static RoomRepository instance;

    private RoomRepository() {
        // Initialisation si nécessaire
    }

    // Singleton Pattern
    public static synchronized RoomRepository getInstance() {
        if (instance == null) {
            instance = new RoomRepository();
        }
        return instance;
    }

    @Override
    public Room save(Room room) {
        String sql = "INSERT INTO rooms (id, type, price, is_available) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET type = ?, price = ?, is_available = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, room.getId());
            pstmt.setString(2, room.getType().name());
            pstmt.setDouble(3, room.getPrice());
            pstmt.setBoolean(4, room.isAvailable());

            pstmt.setString(5, room.getType().name());
            pstmt.setDouble(6, room.getPrice());
            pstmt.setBoolean(7, room.isAvailable());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }

    @Override
    public Optional<Room> findById(Integer id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        Enum.valueOf(com.hotel.enums.RoomType.class, rs.getString("type")),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available")
                );
                return Optional.of(room);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        Enum.valueOf(com.hotel.enums.RoomType.class, rs.getString("type")),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available")
                );
                rooms.add(room);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
