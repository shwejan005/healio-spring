package com.healio.service;

import com.healio.entity.MoodEntry;
import com.healio.repository.MoodEntryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MoodEntryService {

    private final MoodEntryRepository moodEntryRepository;

    public MoodEntryService(MoodEntryRepository moodEntryRepository) {
        this.moodEntryRepository = moodEntryRepository;
    }

    public MoodEntry create(MoodEntry entry) {
        entry.setCreatedAt(System.currentTimeMillis());
        return moodEntryRepository.save(entry);
    }

    public List<MoodEntry> getByUserId(String userId) {
        return moodEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public MoodEntry update(Long id, MoodEntry updates) {
        MoodEntry entry = moodEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mood entry not found"));
        entry.setDate(updates.getDate());
        entry.setMood(updates.getMood());
        entry.setSleepHours(updates.getSleepHours());
        entry.setSleepQuality(updates.getSleepQuality());
        entry.setAnxiety(updates.getAnxiety());
        entry.setStress(updates.getStress());
        entry.setActivities(updates.getActivities());
        entry.setNote(updates.getNote());
        return moodEntryRepository.save(entry);
    }

    public void delete(Long id) {
        moodEntryRepository.deleteById(id);
    }

    public Map<String, Object> getSleepDebt(String userId) {
        List<MoodEntry> entries = moodEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<MoodEntry> recent = entries.size() > 7 ? entries.subList(0, 7) : entries;

        double totalDebt = 0;
        for (MoodEntry entry : recent) {
            double deficit = Math.max(8 - entry.getSleepHours(), 0);
            totalDebt += deficit;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalSleepDebt", Math.round(totalDebt * 10.0) / 10.0);
        return result;
    }
}
