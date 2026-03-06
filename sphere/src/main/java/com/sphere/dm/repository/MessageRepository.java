package com.sphere.dm.repository;

import com.sphere.dm.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);
    int countByConversation_IdAndIsReadFalseAndSender_IdNot(Long conversationId, Long senderId);
}