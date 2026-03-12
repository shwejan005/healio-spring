package com.healio.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        try {
            String query = body.get("query");
            if (query == null || query.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User query is required"));
            }

            String prompt = "You are a supportive mental health companion. Given the following mental health-related query, provide a short, concise, and uplifting response:\n" +
                    "User Query: " + query + ".\n\n" +
                    "Your response should:\n" +
                    "- Be brief and to the point (2-3 short paragraphs maximum)\n" +
                    "- Use a warm, positive tone that makes the user feel happy\n" +
                    "- Provide practical advice in a concise manner\n" +
                    "- Be empathetic but avoid lengthy explanations\n" +
                    "- Use ** for important points or headings\n" +
                    "- Use * for very short bullet points (only when necessary)\n\n" +
                    "Remember to keep your response short and uplifting. Focus on making the user feel better immediately rather than providing comprehensive information.\n" +
                    "For greetings like Hello or Hi, respond with a brief, cheerful greeting and a simple question about how you can help.";

            String payload = objectMapper.writeValueAsString(Map.of(
                    "contents", new Object[]{
                            Map.of("parts", new Object[]{Map.of("text", prompt)})
                    }
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode responseData = objectMapper.readTree(response.body());

            String chatbotResponse = "I apologize, but I was unable to provide a response at this time. How else might I support you today?";
            JsonNode candidates = responseData.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode text = candidates.get(0).path("content").path("parts").get(0).path("text");
                if (!text.isMissingNode()) {
                    chatbotResponse = text.asText();
                }
            }

            return ResponseEntity.ok(Map.of("response", chatbotResponse));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "I encountered an issue while processing your request. Please try again in a moment."));
        }
    }

    @PostMapping("/story")
    public ResponseEntity<Map<String, String>> generateStory() {
        try {
            String[] prompts = {
                    "Generate a short, inspirational story about overcoming challenges and finding inner strength. The story should be about 150-200 words long.",
                    "Write a motivational story about someone who turned their failures into stepping stones for success. Keep it concise and uplifting.",
                    "Create an inspirational story about a person who found hope and positivity during difficult times. The story should be engaging and heartwarming."
            };

            String prompt = prompts[(int) (Math.random() * prompts.length)];

            String payload = objectMapper.writeValueAsString(Map.of(
                    "contents", new Object[]{
                            Map.of("parts", new Object[]{Map.of("text", prompt)})
                    },
                    "generationConfig", Map.of(
                            "temperature", 1,
                            "topP", 0.95,
                            "topK", 40,
                            "maxOutputTokens", 8192
                    )
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode responseData = objectMapper.readTree(response.body());

            String story = "Unable to generate a story at this time.";
            JsonNode candidates = responseData.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode text = candidates.get(0).path("content").path("parts").get(0).path("text");
                if (!text.isMissingNode()) {
                    story = text.asText();
                }
            }

            return ResponseEntity.ok(Map.of("story", story));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to generate story."));
        }
    }
}
