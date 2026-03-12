package com.healio.controller;

import com.healio.entity.Feedback;
import com.healio.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<Feedback> submit(@RequestBody Map<String, Object> body) {
        Integer rating = ((Number) body.get("rating")).intValue();
        String text = (String) body.get("text");
        return ResponseEntity.ok(feedbackService.submit(rating, text));
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAll() {
        return ResponseEntity.ok(feedbackService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer rating = ((Number) body.get("rating")).intValue();
        String text = (String) body.get("text");
        return ResponseEntity.ok(feedbackService.update(id, rating, text));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.ok().build();
    }
}
