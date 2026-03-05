package com.sphere.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private String type;
    private String message;
    private String targetUsername;
    private Long referenceId;
    private LocalDateTime createdAt;
}