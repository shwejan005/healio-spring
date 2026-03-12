package com.healio.repository;

import com.healio.entity.FitnessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FitnessLogRepository extends JpaRepository<FitnessLog, Long> {
    List<FitnessLog> findByUserId(String userId);
    List<FitnessLog> findByUserIdAndCreatedAtGreaterThanEqual(String userId, Long since);
}
