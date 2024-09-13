package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;

import java.util.List;
import java.util.Optional;

public class RoomService {

    private final RoomRepository roomRepository;

    // Constructor injection for RoomRepository
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room create(Room room) {
        return roomRepository.save(room);
    }

    public Optional<Room> getById(Integer id) {
        return roomRepository.findById(id);
    }

    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public Room update(Room room) {
        return roomRepository.save(room);
    }

    public void delete(Integer id) {
        roomRepository.deleteById(id);
    }

    // Additional method to check room availability (logic needs to be implemented)
    public boolean isRoomAvailable(int roomId, String checkIn, String checkOut) {
        // Implement the logic to check room availability
        // For example, by checking existing reservations
        return false; // Replace with actual implementation
    }
}
