package com.healio.repository;

import com.healio.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Long> {
    List<Forum> findAllByOrderByCreatedAtDesc();
    List<Forum> findByAuthorId(Long authorId);
}
