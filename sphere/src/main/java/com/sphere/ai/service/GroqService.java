package com.sphere.ai.service;

import com.sphere.ai.dto.GroqMessage;
import com.sphere.ai.dto.GroqRequest;
import com.sphere.ai.dto.GroqResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.model}")
    private String model;

    private final RestTemplate restTemplate;

    public String summarizeNews(String articleContent) {
        String prompt = "Summarize this news article in 3 sentences: " + articleContent;
        return callGroq(prompt);
    }

    public String moderateContent(String content) {
        String prompt = "Is this content toxic, hateful or spam? Reply with only YES or NO: " + content;
        return callGroq(prompt);
    }

    public String generateFeedInsight(String userActivity) {
        String prompt = "Based on this user activity, give a short personalized insight in 2 sentences: " + userActivity;
        return callGroq(prompt);
    }

    private String callGroq(String prompt) {
        GroqRequest request = new GroqRequest();
        request.setModel(model);
        request.setMax_tokens(500);
        request.setMessages(List.of(
                new GroqMessage("user", prompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<GroqRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GroqResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                GroqResponse.class
        );

        GroqResponse groqResponse = response.getBody();
        if (groqResponse != null && !groqResponse.getChoices().isEmpty()) {
            return groqResponse.getChoices().get(0).getMessage().getContent();
        }
        return "AI service unavailable";
    }
}