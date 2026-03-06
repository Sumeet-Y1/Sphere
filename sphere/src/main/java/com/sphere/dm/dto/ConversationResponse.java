package com.sphere.dm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationResponse {
    private Long id;
    private String otherUsername;
    private String otherAvatarUrl;
    private int unreadCount;
    private LocalDateTime lastMessageAt;
}