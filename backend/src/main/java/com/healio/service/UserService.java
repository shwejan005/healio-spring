package com.healio.service;

import com.healio.entity.User;
import com.healio.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User syncUser(String clerkId, String email, String name, String image) {
        Optional<User> existing = userRepository.findByClerkId(clerkId);
        if (existing.isPresent()) {
            User user = existing.get();
            user.setEmail(email);
            user.setName(name);
            user.setImage(image);
            if (user.getIsPremium() == null) {
                user.setIsPremium(false);
            }
            return userRepository.save(user);
        }
        User newUser = new User(name, email, image, clerkId, false);
        return userRepository.save(newUser);
    }

    public Optional<User> getByClerkId(String clerkId) {
        return userRepository.findByClerkId(clerkId);
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public void makeUserPremium(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsPremium(true);
        userRepository.save(user);
    }
}
