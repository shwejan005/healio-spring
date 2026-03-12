package com.healio.repository;

import com.healio.entity.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUserIdOrderByCreatedAtDesc(String userId);
    List<MoodEntry> findByUserId(String userId);
}
