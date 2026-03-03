package com.sphere.ai.controller;

import com.sphere.ai.service.GroqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GroqService groqService;

    @PostMapping("/summarize")
    public ResponseEntity<String> summarizeNews(@RequestBody String content) {
        return ResponseEntity.ok(groqService.summarizeNews(content));
    }

    @PostMapping("/moderate")
    public ResponseEntity<String> moderateContent(@RequestBody String content) {
        return ResponseEntity.ok(groqService.moderateContent(content));
    }

    @PostMapping("/insight")
    public ResponseEntity<String> generateInsight(@RequestBody String userActivity) {
        return ResponseEntity.ok(groqService.generateFeedInsight(userActivity));
    }
}