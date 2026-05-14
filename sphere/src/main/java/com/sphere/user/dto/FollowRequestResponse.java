package com.sphere.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowRequestResponse {
    private Long id;
    private String requesterUsername;
    private String requesterAvatarUrl;
    private String requesterBio;
    private LocalDateTime createdAt;
}
