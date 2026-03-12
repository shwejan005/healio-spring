package com.healio.service;

import com.healio.entity.Feedback;
import com.healio.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Feedback submit(Integer rating, String text) {
        Feedback feedback = new Feedback(rating, text);
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAll() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    public Feedback update(Long id, Integer rating, String text) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setRating(rating);
        feedback.setText(text);
        return feedbackRepository.save(feedback);
    }

    public void delete(Long id) {
        feedbackRepository.deleteById(id);
    }
}
