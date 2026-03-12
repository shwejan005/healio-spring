package com.healio.controller;

import com.healio.entity.User;
import com.healio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(@RequestBody Map<String, String> body) {
        String clerkId = body.get("clerkId");
        String email = body.get("email");
        String name = body.get("name");
        String image = body.get("image");
        User user = userService.syncUser(clerkId, email, name, image);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{clerkId}")
    public ResponseEntity<User> getUser(@PathVariable String clerkId) {
        return userService.getByClerkId(clerkId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/premium")
    public ResponseEntity<Void> makeUserPremium(@RequestBody Map<String, String> body) {
        userService.makeUserPremium(body.get("email"));
        return ResponseEntity.ok().build();
    }
}
