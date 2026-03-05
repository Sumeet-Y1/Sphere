package com.sphere.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FollowResponse {
    private Long id;
    private String followerUsername;
    private String followingUsername;
    private LocalDateTime createdAt;
}