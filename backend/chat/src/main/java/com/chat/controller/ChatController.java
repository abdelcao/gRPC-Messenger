package com.chat.controller;

import com.chat.entity.Message;
import com.chat.entity.Conversation;
import com.chat.entity.PrivateConversation;
import com.chat.entity.GroupeConversation;
import com.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Conversation endpoints
    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation(@RequestParam Long ownerId) {
        return ResponseEntity.ok(chatService.createConversation(ownerId));
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getConversation(id));
    }

    // Message endpoints
    @PostMapping("/messages")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long userId,
            @RequestParam Long conversationId,
            @RequestParam String text) {
        return ResponseEntity.ok(chatService.sendMessage(userId, conversationId, text));
    }

    @PutMapping("/messages/{id}")
    public ResponseEntity<Message> editMessage(
            @PathVariable Long id,
            @RequestParam String newText) {
        return ResponseEntity.ok(chatService.editMessage(id, newText));
    }

    @PutMapping("/messages/{id}/status")
    public ResponseEntity<Void> updateMessageStatus(
            @PathVariable Long id,
            @RequestParam Message.MessageStatus status) {
        chatService.updateMessageStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<Message>> getConversationMessages(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getConversationMessages(id));
    }

    // Private conversation endpoints
    @PostMapping("/private-conversations")
    public ResponseEntity<PrivateConversation> createPrivateConversation(
            @RequestParam Long ownerId,
            @RequestParam Long receiverId) {
        return ResponseEntity.ok(chatService.createPrivateConversation(ownerId, receiverId));
    }

    @GetMapping("/private-conversations/{id}")
    public ResponseEntity<PrivateConversation> getPrivateConversation(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getPrivateConversation(id));
    }

    // Group conversation endpoints
    @PostMapping("/group-conversations")
    public ResponseEntity<GroupeConversation> createGroupConversation(
            @RequestParam Long ownerId,
            @RequestParam String name) {
        return ResponseEntity.ok(chatService.createGroupConversation(ownerId, name));
    }

    @GetMapping("/group-conversations/{id}")
    public ResponseEntity<GroupeConversation> getGroupConversation(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getGroupConversation(id));
    }

    @PostMapping("/group-conversations/{id}/members")
    public ResponseEntity<Void> addMemberToGroup(
            @PathVariable Long id,
            @RequestParam Long userId) {
        chatService.addMemberToGroup(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/group-conversations/{id}/members")
    public ResponseEntity<Void> removeMemberFromGroup(
            @PathVariable Long id,
            @RequestParam Long userId) {
        chatService.removeMemberFromGroup(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/group-conversations/{id}/admin")
    public ResponseEntity<Void> makeGroupAdmin(
            @PathVariable Long id,
            @RequestParam Long userId) {
        chatService.makeGroupAdmin(id, userId);
        return ResponseEntity.ok().build();
    }
} 