package com.healio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healio.entity.Forum;
import com.healio.entity.ForumComment;
import com.healio.entity.User;
import com.healio.repository.ForumCommentRepository;
import com.healio.repository.ForumRepository;
import com.healio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ForumService {

    private final ForumRepository forumRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ForumService(ForumRepository forumRepository, ForumCommentRepository forumCommentRepository,
                        UserRepository userRepository, ObjectMapper objectMapper) {
        this.forumRepository = forumRepository;
        this.forumCommentRepository = forumCommentRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public Forum create(String clerkId, String title, String content) {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Forum forum = new Forum();
        forum.setTitle(title);
        forum.setContent(content);
        forum.setAuthorId(user.getId());
        forum.setVotes("{}");
        forum.setCreatedAt(System.currentTimeMillis());
        return forumRepository.save(forum);
    }

    public List<Map<String, Object>> getForums() {
        List<Forum> forums = forumRepository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Forum forum : forums) {
            Map<String, Object> forumMap = new HashMap<>();
            forumMap.put("_id", forum.getId().toString());
            forumMap.put("title", forum.getTitle());
            forumMap.put("content", forum.getContent());
            forumMap.put("authorId", forum.getAuthorId().toString());
            forumMap.put("createdAt", forum.getCreatedAt());

            // Get author
            Optional<User> author = userRepository.findById(forum.getAuthorId());
            author.ifPresent(u -> {
                Map<String, Object> authorMap = new HashMap<>();
                authorMap.put("_id", u.getId().toString());
                authorMap.put("name", u.getName());
                authorMap.put("email", u.getEmail());
                authorMap.put("image", u.getImage());
                authorMap.put("clerkId", u.getClerkId());
                forumMap.put("author", authorMap);
            });

            // Get comments with authors
            List<ForumComment> comments = forumCommentRepository.findByForumId(forum.getId());
            List<Map<String, Object>> commentsList = new ArrayList<>();
            for (ForumComment comment : comments) {
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("_id", comment.getId().toString());
                commentMap.put("forumId", comment.getForumId().toString());
                commentMap.put("authorId", comment.getAuthorId().toString());
                commentMap.put("content", comment.getContent());
                commentMap.put("createdAt", comment.getCreatedAt());

                Optional<User> commentAuthor = userRepository.findById(comment.getAuthorId());
                commentAuthor.ifPresent(u -> {
                    Map<String, Object> cAuthorMap = new HashMap<>();
                    cAuthorMap.put("_id", u.getId().toString());
                    cAuthorMap.put("name", u.getName());
                    cAuthorMap.put("image", u.getImage());
                    cAuthorMap.put("clerkId", u.getClerkId());
                    commentMap.put("author", cAuthorMap);
                });

                commentsList.add(commentMap);
            }
            forumMap.put("comments", commentsList);

            // Calculate votes
            try {
                Map<String, Integer> votes = objectMapper.readValue(
                        forum.getVotes() != null ? forum.getVotes() : "{}",
                        new TypeReference<Map<String, Integer>>() {});
                int upvotes = 0;
                int dislikes = 0;
                for (Integer val : votes.values()) {
                    if (val == 1) upvotes++;
                    else if (val == -1) dislikes++;
                }
                forumMap.put("upvotes", upvotes);
                forumMap.put("dislikes", dislikes);
                forumMap.put("votes", votes);
            } catch (Exception e) {
                forumMap.put("upvotes", 0);
                forumMap.put("dislikes", 0);
                forumMap.put("votes", new HashMap<>());
            }

            result.add(forumMap);
        }

        return result;
    }

    public void vote(Long forumId, String clerkId, int voteValue) {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum not found"));

        try {
            Map<String, Integer> votes = objectMapper.readValue(
                    forum.getVotes() != null ? forum.getVotes() : "{}",
                    new TypeReference<Map<String, Integer>>() {});

            String odId = user.getId().toString();
            if (voteValue == 0) {
                votes.remove(odId);
            } else {
                votes.put(odId, voteValue);
            }

            forum.setVotes(objectMapper.writeValueAsString(votes));
            forumRepository.save(forum);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process vote", e);
        }
    }

    public ForumComment addComment(Long forumId, String clerkId, String content) {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ForumComment comment = new ForumComment();
        comment.setForumId(forumId);
        comment.setAuthorId(user.getId());
        comment.setContent(content);
        comment.setCreatedAt(System.currentTimeMillis());
        return forumCommentRepository.save(comment);
    }

    @Transactional
    public void removeForum(Long forumId, String clerkId) {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum not found"));

        if (!forum.getAuthorId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }

        forumCommentRepository.deleteByForumId(forumId);
        forumRepository.delete(forum);
    }
}
