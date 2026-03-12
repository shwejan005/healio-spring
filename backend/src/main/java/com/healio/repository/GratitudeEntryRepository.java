package com.healio.repository;

import com.healio.entity.GratitudeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GratitudeEntryRepository extends JpaRepository<GratitudeEntry, Long> {
    List<GratitudeEntry> findByUserIdOrderByDateDesc(String userId);
}
