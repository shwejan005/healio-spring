package com.healio.repository;

import com.healio.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findTop50ByRoomIdOrderByTimestampDesc(Long roomId);
}
