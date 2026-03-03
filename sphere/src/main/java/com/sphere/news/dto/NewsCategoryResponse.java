package com.sphere.news.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewsCategoryResponse {
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String sourceName;
    private String author;
}