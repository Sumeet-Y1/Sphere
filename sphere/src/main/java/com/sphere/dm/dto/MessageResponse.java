package com.sphere.dm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private String senderUsername;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
}