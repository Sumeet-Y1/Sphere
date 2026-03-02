package com.sphere.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String authorUsername;
    private Long postId;
    private Long parentId;
    private int upvotes;
    private List<CommentResponse> replies;
    private LocalDateTime createdAt;
}