package com.sphere.post.dto;

import com.sphere.post.MediaType;
import com.sphere.post.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String content;
    private String imageUrl;
    private String linkUrl;

    private List<String> mediaUrls;
    private MediaType mediaType;

    @NotNull(message = "Post type is required")
    private PostType type;

    @NotNull(message = "Community ID is required")
    private Long communityId;
}