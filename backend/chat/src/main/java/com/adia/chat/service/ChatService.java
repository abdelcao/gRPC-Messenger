package com.adia.chat.service;

import java.util.List;

import com.adia.chat.entity.Conversation;
import com.adia.chat.entity.GroupeConversation;
import com.adia.chat.entity.Message;
import com.adia.chat.entity.PrivateConversation;

public interface ChatService {
    // Conversation methods
    Conversation createConversation(Long ownerId);
    Conversation getConversation(Long id);
    
    // Message methods
    Message sendMessage(Long userId, Long conversationId, String text);
    Message editMessage(Long messageId, String newText);
    void updateMessageStatus(Long messageId, Message.MessageStatus status);
    List<Message> getConversationMessages(Long conversationId);
    
    // Private conversation methods
    PrivateConversation createPrivateConversation(Long ownerId, Long receiverId);
    PrivateConversation getPrivateConversation(Long conversationId);
    
    // Group conversation methods
    GroupeConversation createGroupConversation(Long ownerId, String name);
    GroupeConversation getGroupConversation(Long conversationId);
    void addMemberToGroup(Long groupId, Long userId);
    void removeMemberFromGroup(Long groupId, Long userId);
    void makeGroupAdmin(Long groupId, Long userId);
} 