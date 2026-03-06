package com.sphere.dm.service;

import com.sphere.dm.dto.ConversationResponse;
import com.sphere.dm.dto.MessageResponse;
import com.sphere.dm.dto.SendMessageRequest;
import com.sphere.dm.entity.Conversation;
import com.sphere.dm.entity.Message;
import com.sphere.dm.repository.ConversationRepository;
import com.sphere.dm.repository.MessageRepository;
import com.sphere.notifications.service.NotificationService;
import com.sphere.user.User;
import com.sphere.user.repository.BlockRepository;
import com.sphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.sphere.common.config.RateLimitService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DmService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RateLimitService rateLimitService;

    public MessageResponse sendMessage(SendMessageRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!rateLimitService.allowDm(email)) {
            throw new RuntimeException("DM rate limit exceeded. Maximum 50 DMs per hour!");
        }

        User receiver = userRepository.findByUsername(request.getReceiverUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (blockRepository.existsByBlockerAndBlocked(receiver, sender)) {
            throw new RuntimeException("You cannot message this user");
        }

        if (blockRepository.existsByBlockerAndBlocked(sender, receiver)) {
            throw new RuntimeException("You have blocked this user");
        }

        Conversation conversation = conversationRepository
                .findByUsers(sender, receiver)
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.builder()
                            .user1(sender)
                            .user2(receiver)
                            .build();
                    return conversationRepository.save(newConversation);
                });

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent())
                .build();

        messageRepository.save(message);

        MessageResponse response = mapToMessageResponse(message);

        // send real time message via WebSocket
        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/messages",
                response
        );

        // send notification
        notificationService.sendNotification(
                receiver.getUsername(),
                "DM",
                sender.getUsername() + " sent you a message",
                conversation.getId()
        );

        return response;
    }

    public List<ConversationResponse> getConversations() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return conversationRepository.findAllByUser(user)
                .stream()
                .map(c -> mapToConversationResponse(c, user))
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getMessages(Long conversationId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUser1().getId().equals(user.getId()) &&
                !conversation.getUser2().getId().equals(user.getId())) {
            throw new RuntimeException("You are not part of this conversation");
        }

        // mark messages as read
        List<Message> messages = messageRepository
                .findByConversation_IdOrderByCreatedAtAsc(conversationId);

        messages.stream()
                .filter(m -> !m.getSender().getId().equals(user.getId()) && !m.isRead())
                .forEach(m -> {
                    m.setRead(true);
                    messageRepository.save(m);
                });

        return messages.stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    public void deleteMessage(Long messageId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own messages");
        }

        messageRepository.delete(message);
    }

    private ConversationResponse mapToConversationResponse(Conversation conversation, User currentUser) {
        User otherUser = conversation.getUser1().getId().equals(currentUser.getId())
                ? conversation.getUser2()
                : conversation.getUser1();

        int unreadCount = messageRepository
                .countByConversation_IdAndIsReadFalseAndSender_IdNot(
                        conversation.getId(), currentUser.getId());

        return ConversationResponse.builder()
                .id(conversation.getId())
                .otherUsername(otherUser.getUsername())
                .otherAvatarUrl(otherUser.getAvatarUrl())
                .unreadCount(unreadCount)
                .lastMessageAt(conversation.getLastMessageAt())
                .build();
    }

    private MessageResponse mapToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderUsername(message.getSender().getUsername())
                .content(message.getContent())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}