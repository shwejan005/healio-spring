package com.healio.controller;

import com.healio.entity.MoodEntry;
import com.healio.service.MoodEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mood-entries")
public class MoodEntryController {

    private final MoodEntryService moodEntryService;

    public MoodEntryController(MoodEntryService moodEntryService) {
        this.moodEntryService = moodEntryService;
    }

    @PostMapping
    public ResponseEntity<MoodEntry> create(@RequestBody MoodEntry entry) {
        return ResponseEntity.ok(moodEntryService.create(entry));
    }

    @GetMapping
    public ResponseEntity<List<MoodEntry>> getByUserId(@RequestParam String userId) {
        return ResponseEntity.ok(moodEntryService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoodEntry> update(@PathVariable Long id, @RequestBody MoodEntry entry) {
        return ResponseEntity.ok(moodEntryService.update(id, entry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moodEntryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sleep-debt")
    public ResponseEntity<Map<String, Object>> getSleepDebt(@RequestParam String userId) {
        return ResponseEntity.ok(moodEntryService.getSleepDebt(userId));
    }
}
