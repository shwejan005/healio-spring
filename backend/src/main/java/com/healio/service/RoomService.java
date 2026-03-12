package com.healio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healio.entity.Room;
import com.healio.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

    public RoomService(RoomRepository roomRepository, ObjectMapper objectMapper) {
        this.roomRepository = roomRepository;
        this.objectMapper = objectMapper;
    }

    public Room create(String name, Integer maxUsers) {
        Room room = new Room(name, maxUsers);
        return roomRepository.save(room);
    }

    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public Room joinRoom(Long roomId, String username) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getCurrentUsers() >= room.getMaxUsers()) {
            throw new RuntimeException("Room is full");
        }

        List<String> activeUsers = parseActiveUsers(room.getActiveUsers());
        activeUsers.add(username);

        room.setCurrentUsers(room.getCurrentUsers() + 1);
        room.setActiveUsers(serializeActiveUsers(activeUsers));
        return roomRepository.save(room);
    }

    public Room leaveRoom(Long roomId, String username) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<String> activeUsers = parseActiveUsers(room.getActiveUsers());
        activeUsers.remove(username);

        room.setCurrentUsers(Math.max(0, room.getCurrentUsers() - 1));
        room.setActiveUsers(serializeActiveUsers(activeUsers));
        return roomRepository.save(room);
    }

    private List<String> parseActiveUsers(String json) {
        try {
            if (json == null || json.isEmpty()) return new ArrayList<>();
            List<String> list = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            return new ArrayList<>(list);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String serializeActiveUsers(List<String> activeUsers) {
        try {
            return objectMapper.writeValueAsString(activeUsers);
        } catch (Exception e) {
            return "[]";
        }
    }
}
