package com.healio.controller;

import com.healio.entity.GratitudeEntry;
import com.healio.service.GratitudeEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gratitude")
public class GratitudeController {

    private final GratitudeEntryService gratitudeEntryService;

    public GratitudeController(GratitudeEntryService gratitudeEntryService) {
        this.gratitudeEntryService = gratitudeEntryService;
    }

    @PostMapping
    public ResponseEntity<GratitudeEntry> create(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String gratitude = body.get("gratitude");
        return ResponseEntity.ok(gratitudeEntryService.create(userId, gratitude));
    }

    @GetMapping
    public ResponseEntity<List<GratitudeEntry>> getByUserId(@RequestParam String userId) {
        return ResponseEntity.ok(gratitudeEntryService.getByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gratitudeEntryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
