package com.sphere.post.dto;

import com.sphere.post.PostType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String linkUrl;
    private PostType type;
    private String authorUsername;
    private String communityName;
    private int upvotes;
    private int downvotes;
    private int commentCount;
    private LocalDateTime createdAt;
}