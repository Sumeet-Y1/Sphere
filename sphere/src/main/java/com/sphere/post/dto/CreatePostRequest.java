package com.sphere.post.dto;

import com.sphere.post.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String content;
    private String imageUrl;
    private String linkUrl;

    @NotNull(message = "Post type is required")
    private PostType type;

    @NotNull(message = "Community ID is required")
    private Long communityId;
}