package com.sphere.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Post ID is required")
    private Long postId;

    private Long parentId;
}