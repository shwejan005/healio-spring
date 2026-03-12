package com.healio.controller;

import com.healio.entity.ForumComment;
import com.healio.service.ForumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forums")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(forumService.create(body.get("clerkId"), body.get("title"), body.get("content")));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getForums() {
        return ResponseEntity.ok(forumService.getForums());
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<Void> vote(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String clerkId = (String) body.get("clerkId");
        int voteValue = ((Number) body.get("vote")).intValue();
        forumService.vote(id, clerkId, voteValue);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ForumComment> addComment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(forumService.addComment(id, body.get("clerkId"), body.get("content")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeForum(@PathVariable Long id, @RequestParam String clerkId) {
        forumService.removeForum(id, clerkId);
        return ResponseEntity.ok().build();
    }
}
