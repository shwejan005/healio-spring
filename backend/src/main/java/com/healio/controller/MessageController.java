package com.healio.controller;

import com.healio.entity.Message;
import com.healio.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Message> send(@RequestBody Map<String, Object> body) {
        Long roomId = ((Number) body.get("roomId")).longValue();
        String sender = (String) body.get("sender");
        String text = (String) body.get("text");
        return ResponseEntity.ok(messageService.send(roomId, sender, text));
    }

    @GetMapping
    public ResponseEntity<List<Message>> getByRoomId(@RequestParam Long roomId) {
        return ResponseEntity.ok(messageService.getByRoomId(roomId));
    }
}
