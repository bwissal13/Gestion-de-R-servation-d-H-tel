package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;

import java.util.List;
import java.util.Optional;

public class RoomService implements Service<Room, Integer> {

    private final RoomRepository roomRepository = RoomRepository.getInstance();

    @Override
    public Room create(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> getById(Integer id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room update(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public void delete(Integer id) {
        roomRepository.deleteById(id);
    }

    // Méthode supplémentaire pour vérifier la disponibilité
    public boolean isRoomAvailable(int roomId, String checkIn, String checkOut) {
        // Implémenter la logique pour vérifier la disponibilité de la chambre
        // Par exemple, en vérifiant les réservations existantes
        return false; // Remplacer par l'implémentation réelle
    }
}
