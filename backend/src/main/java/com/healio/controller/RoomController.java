package com.healio.controller;

import com.healio.entity.Room;
import com.healio.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Integer maxUsers = ((Number) body.get("maxUsers")).intValue();
        return ResponseEntity.ok(roomService.create(name, maxUsers));
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Room> joinRoom(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(roomService.joinRoom(id, body.get("username")));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Room> leaveRoom(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(roomService.leaveRoom(id, body.get("username")));
    }
}
