package com.healio.service;

import com.healio.entity.FitnessLog;
import com.healio.repository.FitnessLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FitnessLogService {

    private final FitnessLogRepository fitnessLogRepository;

    public FitnessLogService(FitnessLogRepository fitnessLogRepository) {
        this.fitnessLogRepository = fitnessLogRepository;
    }

    public FitnessLog log(String userId, String workoutType, Integer duration, Integer caloriesBurned) {
        FitnessLog fitnessLog = new FitnessLog(userId, workoutType, duration, caloriesBurned);
        return fitnessLogRepository.save(fitnessLog);
    }

    public List<FitnessLog> getByUserId(String userId) {
        return fitnessLogRepository.findByUserId(userId);
    }

    public List<FitnessLog> getWeeklyLogs(String userId) {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        return fitnessLogRepository.findByUserIdAndCreatedAtGreaterThanEqual(userId, sevenDaysAgo);
    }

    public FitnessLog update(Long id, String workoutType, Integer duration, Integer caloriesBurned) {
        FitnessLog log = fitnessLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fitness log not found"));
        if (workoutType != null) log.setWorkoutType(workoutType);
        if (duration != null) log.setDuration(duration);
        if (caloriesBurned != null) log.setCaloriesBurned(caloriesBurned);
        return fitnessLogRepository.save(log);
    }

    public void delete(Long id) {
        fitnessLogRepository.deleteById(id);
    }
}
