package com.healio.controller;

import com.healio.entity.FitnessLog;
import com.healio.service.FitnessLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fitness-logs")
public class FitnessLogController {

    private final FitnessLogService fitnessLogService;

    public FitnessLogController(FitnessLogService fitnessLogService) {
        this.fitnessLogService = fitnessLogService;
    }

    @PostMapping
    public ResponseEntity<FitnessLog> log(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        String workoutType = (String) body.get("workoutType");
        Integer duration = ((Number) body.get("duration")).intValue();
        Integer caloriesBurned = ((Number) body.get("caloriesBurned")).intValue();
        return ResponseEntity.ok(fitnessLogService.log(userId, workoutType, duration, caloriesBurned));
    }

    @GetMapping
    public ResponseEntity<List<FitnessLog>> getByUserId(@RequestParam String userId) {
        return ResponseEntity.ok(fitnessLogService.getByUserId(userId));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<FitnessLog>> getWeeklyLogs(@RequestParam String userId) {
        return ResponseEntity.ok(fitnessLogService.getWeeklyLogs(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FitnessLog> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String workoutType = (String) body.get("workoutType");
        Integer duration = body.get("duration") != null ? ((Number) body.get("duration")).intValue() : null;
        Integer caloriesBurned = body.get("caloriesBurned") != null ? ((Number) body.get("caloriesBurned")).intValue() : null;
        return ResponseEntity.ok(fitnessLogService.update(id, workoutType, duration, caloriesBurned));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fitnessLogService.delete(id);
        return ResponseEntity.ok().build();
    }
}
