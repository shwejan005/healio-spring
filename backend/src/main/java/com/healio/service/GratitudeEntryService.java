package com.healio.service;

import com.healio.entity.GratitudeEntry;
import com.healio.repository.GratitudeEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GratitudeEntryService {

    private final GratitudeEntryRepository gratitudeEntryRepository;

    public GratitudeEntryService(GratitudeEntryRepository gratitudeEntryRepository) {
        this.gratitudeEntryRepository = gratitudeEntryRepository;
    }

    public GratitudeEntry create(String userId, String gratitude) {
        GratitudeEntry entry = new GratitudeEntry(userId, Instant.now().toString(), gratitude);
        return gratitudeEntryRepository.save(entry);
    }

    public List<GratitudeEntry> getByUserId(String userId) {
        return gratitudeEntryRepository.findByUserIdOrderByDateDesc(userId);
    }

    public void delete(Long id) {
        gratitudeEntryRepository.deleteById(id);
    }
}
