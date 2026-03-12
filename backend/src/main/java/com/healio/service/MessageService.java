package com.healio.service;

import com.healio.entity.Message;
import com.healio.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message send(Long roomId, String sender, String text) {
        Message message = new Message(roomId, sender, text);
        return messageRepository.save(message);
    }

    public List<Message> getByRoomId(Long roomId) {
        return messageRepository.findTop50ByRoomIdOrderByTimestampDesc(roomId);
    }
}
