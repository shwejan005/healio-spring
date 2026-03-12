package com.healio.controller;

import com.healio.entity.Goal;
import com.healio.service.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<Goal> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(goalService.create(body.get("userId"), body.get("title")));
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getByUserId(@RequestParam String userId) {
        return ResponseEntity.ok(goalService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(goalService.updateStatus(id, body.get("completed")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.ok().build();
    }
}
