package com.sphere.news.service;

import com.sphere.news.dto.NewsCategoryResponse;
import com.sphere.news.dto.NewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public List<NewsCategoryResponse> getNewsByCategory(String category) {
        String url = apiUrl + "/top-headlines?category=" + category + "&language=en&apiKey=" + apiKey;
        NewsResponse response = restTemplate.getForObject(url, NewsResponse.class);
        return mapToResponse(response);
    }

    public List<NewsCategoryResponse> searchNews(String query) {
        String url = apiUrl + "/everything?q=" + query + "&language=en&sortBy=publishedAt&apiKey=" + apiKey;
        NewsResponse response = restTemplate.getForObject(url, NewsResponse.class);
        return mapToResponse(response);
    }

    public List<NewsCategoryResponse> getTopHeadlines() {
        String url = apiUrl + "/top-headlines?language=en&apiKey=" + apiKey;
        NewsResponse response = restTemplate.getForObject(url, NewsResponse.class);
        return mapToResponse(response);
    }

    private List<NewsCategoryResponse> mapToResponse(NewsResponse response) {
        if (response == null || response.getArticles() == null) {
            return List.of();
        }
        return response.getArticles().stream()
                .filter(a -> a.getTitle() != null && !a.getTitle().equals("[Removed]"))
                .map(article -> NewsCategoryResponse.builder()
                        .title(article.getTitle())
                        .description(article.getDescription())
                        .url(article.getUrl())
                        .urlToImage(article.getUrlToImage())
                        .publishedAt(article.getPublishedAt())
                        .sourceName(article.getSource() != null ? article.getSource().getName() : null)
                        .author(article.getAuthor())
                        .build())
                .collect(Collectors.toList());
    }
}