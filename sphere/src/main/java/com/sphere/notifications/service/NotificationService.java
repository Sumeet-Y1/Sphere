package com.sphere.notifications.service;

import com.sphere.notifications.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String targetUsername, String type, String message, Long referenceId) {
        NotificationEvent event = NotificationEvent.builder()
                .type(type)
                .message(message)
                .targetUsername(targetUsername)
                .referenceId(referenceId)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(
                targetUsername,
                "/queue/notifications",
                event
        );
    }


    public void notifyComment(String postAuthorUsername, String commenterUsername, Long postId) {
        System.out.println("🔔 Sending notification to: " + postAuthorUsername);
        sendNotification(
                postAuthorUsername,
                "COMMENT",
                commenterUsername + " commented on your post",
                postId
        );
    }

    public void notifyUpvote(String postAuthorUsername, String voterUsername, Long postId) {
        sendNotification(
                postAuthorUsername,
                "UPVOTE",
                voterUsername + " upvoted your post",
                postId
        );
    }

    public void notifyNewMember(String communityOwnerUsername, String newMemberUsername, Long communityId) {
        sendNotification(
                communityOwnerUsername,
                "NEW_MEMBER",
                newMemberUsername + " joined your community",
                communityId
        );
    }
}

