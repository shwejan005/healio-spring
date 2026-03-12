package com.healio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healio.entity.*;
import com.healio.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final MoodEntryRepository moodEntryRepository;
    private final GratitudeEntryRepository gratitudeEntryRepository;
    private final ForumRepository forumRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final GoalRepository goalRepository;
    private final FitnessLogRepository fitnessLogRepository;
    private final ObjectMapper objectMapper;

    public AccountService(UserRepository userRepository, MoodEntryRepository moodEntryRepository,
                          GratitudeEntryRepository gratitudeEntryRepository, ForumRepository forumRepository,
                          ForumCommentRepository forumCommentRepository, GoalRepository goalRepository,
                          FitnessLogRepository fitnessLogRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.moodEntryRepository = moodEntryRepository;
        this.gratitudeEntryRepository = gratitudeEntryRepository;
        this.forumRepository = forumRepository;
        this.forumCommentRepository = forumCommentRepository;
        this.goalRepository = goalRepository;
        this.fitnessLogRepository = fitnessLogRepository;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> getMoodStats(String userId) {
        List<MoodEntry> entries = moodEntryRepository.findByUserId(userId);
        Map<String, Object> stats = new HashMap<>();

        if (entries.isEmpty()) {
            stats.put("averageMood", 0);
            stats.put("averageSleep", 0);
            stats.put("averageAnxiety", 0);
            stats.put("averageStress", 0);
            stats.put("moodTrend", new ArrayList<>());
            stats.put("commonActivities", new ArrayList<>());
            return stats;
        }

        double avgMood = entries.stream().mapToInt(MoodEntry::getMood).average().orElse(0);
        double avgSleep = entries.stream().mapToDouble(MoodEntry::getSleepHours).average().orElse(0);
        double avgAnxiety = entries.stream().mapToInt(MoodEntry::getAnxiety).average().orElse(0);
        double avgStress = entries.stream().mapToInt(MoodEntry::getStress).average().orElse(0);

        stats.put("averageMood", avgMood);
        stats.put("averageSleep", avgSleep);
        stats.put("averageAnxiety", avgAnxiety);
        stats.put("averageStress", avgStress);

        // Mood trend (last 7)
        List<MoodEntry> sorted = entries.stream()
                .sorted(Comparator.comparing(MoodEntry::getDate).reversed())
                .limit(7)
                .collect(Collectors.toList());
        Collections.reverse(sorted);
        List<Map<String, Object>> moodTrend = sorted.stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("date", e.getDate());
            m.put("mood", e.getMood());
            return m;
        }).collect(Collectors.toList());
        stats.put("moodTrend", moodTrend);

        // Common activities
        Map<String, Integer> activityCount = new HashMap<>();
        for (MoodEntry entry : entries) {
            try {
                List<String> activities = objectMapper.readValue(
                        entry.getActivities() != null ? entry.getActivities() : "[]",
                        new TypeReference<List<String>>() {});
                for (String a : activities) {
                    activityCount.merge(a, 1, Integer::sum);
                }
            } catch (Exception e) {
                // skip
            }
        }
        List<Map<String, Object>> commonActivities = activityCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("activity", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
        stats.put("commonActivities", commonActivities);

        return stats;
    }

    public Map<String, Object> getUserAnalytics(String userId) {
        Map<String, Object> analytics = new HashMap<>();

        // User info
        Optional<User> userOpt = userRepository.findByClerkId(userId);
        analytics.put("user", userOpt.map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("_id", u.getId().toString());
            m.put("name", u.getName());
            m.put("email", u.getEmail());
            m.put("image", u.getImage());
            m.put("clerkId", u.getClerkId());
            return m;
        }).orElse(null));

        // Mood stats
        analytics.put("moodStats", getMoodStats(userId));

        // Gratitude stats
        List<GratitudeEntry> gratitudeEntries = gratitudeEntryRepository.findByUserIdOrderByDateDesc(userId);
        Map<String, Object> gratitudeStats = new HashMap<>();
        gratitudeStats.put("totalEntries", gratitudeEntries.size());
        gratitudeStats.put("recentEntries", gratitudeEntries.stream().limit(5).collect(Collectors.toList()));
        analytics.put("gratitudeStats", gratitudeStats);

        // Forum stats
        Map<String, Object> forumStats = new HashMap<>();
        if (userOpt.isPresent()) {
            Long dbUserId = userOpt.get().getId();
            List<Forum> posts = forumRepository.findByAuthorId(dbUserId);
            List<ForumComment> comments = forumCommentRepository.findByAuthorId(dbUserId);
            forumStats.put("totalPosts", posts.size());
            forumStats.put("totalComments", comments.size());

            List<Map<String, Object>> recentActivity = new ArrayList<>();
            for (Forum p : posts) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", "post");
                m.put("title", p.getTitle());
                m.put("date", Instant.ofEpochMilli(p.getCreatedAt()).toString());
                m.put("id", p.getId().toString());
                recentActivity.add(m);
            }
            for (ForumComment c : comments) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", "comment");
                String content = c.getContent();
                m.put("content", content.length() > 50 ? content.substring(0, 50) + "..." : content);
                m.put("date", Instant.ofEpochMilli(c.getCreatedAt()).toString());
                m.put("id", c.getId().toString());
                recentActivity.add(m);
            }
            recentActivity.sort((a, b) -> ((String) b.get("date")).compareTo((String) a.get("date")));
            forumStats.put("recentActivity", recentActivity.stream().limit(5).collect(Collectors.toList()));
        } else {
            forumStats.put("totalPosts", 0);
            forumStats.put("totalComments", 0);
            forumStats.put("recentActivity", new ArrayList<>());
        }
        analytics.put("forumStats", forumStats);

        // Goals stats
        List<Goal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
        long completedGoals = goals.stream().filter(Goal::getCompleted).count();
        Map<String, Object> goalsStats = new HashMap<>();
        goalsStats.put("totalGoals", goals.size());
        goalsStats.put("completedGoals", completedGoals);
        goalsStats.put("completionRate", goals.isEmpty() ? 0 : (completedGoals * 100.0 / goals.size()));
        goalsStats.put("activeGoals", goals.stream().filter(g -> !g.getCompleted()).collect(Collectors.toList()));
        analytics.put("goalsStats", goalsStats);

        // Fitness stats
        List<FitnessLog> fitnessLogs = fitnessLogRepository.findByUserId(userId);
        Map<String, Object> fitnessStats = new HashMap<>();
        if (fitnessLogs.isEmpty()) {
            fitnessStats.put("totalWorkouts", 0);
            fitnessStats.put("totalDuration", 0);
            fitnessStats.put("totalCaloriesBurned", 0);
            fitnessStats.put("workoutTypes", new ArrayList<>());
            fitnessStats.put("recentWorkouts", new ArrayList<>());
        } else {
            int totalWorkouts = fitnessLogs.size();
            int totalDuration = fitnessLogs.stream().mapToInt(FitnessLog::getDuration).sum();
            int totalCalories = fitnessLogs.stream().mapToInt(FitnessLog::getCaloriesBurned).sum();

            Map<String, Long> typeCount = fitnessLogs.stream()
                    .collect(Collectors.groupingBy(FitnessLog::getWorkoutType, Collectors.counting()));
            List<Map<String, Object>> workoutTypes = typeCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .map(e -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("type", e.getKey());
                        m.put("count", e.getValue());
                        m.put("percentage", (e.getValue() * 100.0) / totalWorkouts);
                        return m;
                    }).collect(Collectors.toList());

            List<FitnessLog> recentWorkouts = fitnessLogs.stream()
                    .sorted(Comparator.comparing(FitnessLog::getCreatedAt).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            fitnessStats.put("totalWorkouts", totalWorkouts);
            fitnessStats.put("totalDuration", totalDuration);
            fitnessStats.put("totalCaloriesBurned", totalCalories);
            fitnessStats.put("workoutTypes", workoutTypes);
            fitnessStats.put("recentWorkouts", recentWorkouts);
        }
        analytics.put("fitnessStats", fitnessStats);

        return analytics;
    }
}
