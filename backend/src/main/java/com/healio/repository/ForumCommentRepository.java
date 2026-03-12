package com.healio.repository;

import com.healio.entity.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    List<ForumComment> findByForumId(Long forumId);
    List<ForumComment> findByAuthorId(Long authorId);
    void deleteByForumId(Long forumId);
}
