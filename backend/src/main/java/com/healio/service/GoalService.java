package com.healio.service;

import com.healio.entity.Goal;
import com.healio.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal create(String userId, String title) {
        Goal goal = new Goal(userId, title);
        return goalRepository.save(goal);
    }

    public List<Goal> getByUserId(String userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Goal updateStatus(Long id, Boolean completed) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        goal.setCompleted(completed);
        return goalRepository.save(goal);
    }

    public void delete(Long id) {
        goalRepository.deleteById(id);
    }
}
