package com.sphere.community.dto;

import com.sphere.community.CommunityType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityResponse {
    private Long id;
    private String name;
    private String description;
    private String bannerUrl;
    private String avatarUrl;
    private CommunityType type;
    private String ownerUsername;
    private int memberCount;
    private LocalDateTime createdAt;
}