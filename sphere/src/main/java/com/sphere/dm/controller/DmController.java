package com.sphere.dm.controller;

import com.sphere.dm.dto.ConversationResponse;
import com.sphere.dm.dto.MessageResponse;
import com.sphere.dm.dto.SendMessageRequest;
import com.sphere.dm.service.DmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dm")
@RequiredArgsConstructor
public class DmController {

    private final DmService dmService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(dmService.sendMessage(request));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations() {
        return ResponseEntity.ok(dmService.getConversations());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(dmService.getMessages(conversationId));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        dmService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}