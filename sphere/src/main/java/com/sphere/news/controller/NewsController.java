package com.sphere.news.controller;

import com.sphere.news.dto.NewsCategoryResponse;
import com.sphere.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsCategoryResponse>> getTopHeadlines() {
        return ResponseEntity.ok(newsService.getTopHeadlines());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<NewsCategoryResponse>> getNewsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(newsService.getNewsByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NewsCategoryResponse>> searchNews(@RequestParam String query) {
        return ResponseEntity.ok(newsService.searchNews(query));
    }
}