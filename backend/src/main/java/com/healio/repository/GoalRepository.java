package com.healio.repository;

import com.healio.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserIdOrderByCreatedAtDesc(String userId);
}
