package com.adia.chat.service;

import java.util.List;
import java.util.stream.Stream;

import com.adia.chat.entity.Conversation;
import com.adia.chat.entity.ConversationWithLastMessage;
import com.adia.chat.entity.GroupeConversation;
import com.adia.chat.entity.Message;
import com.adia.chat.entity.PrivateConversation;

public interface ChatService {
    // Conversation methods
    Conversation createConversation(Integer ownerId);
    Conversation getConversation(Integer id);
    
    // Message methods
    Message sendMessage(Integer userId, Integer conversationId, String text);
    Message editMessage(Integer messageId, String newText);
    void updateMessageStatus(Integer messageId, Message.MessageStatus status);
    List<Message> getConversationMessages(Integer conversationId);
    
    // Private conversation methods
    PrivateConversation createPrivateConversation(Integer ownerId, Integer receiverId);
    PrivateConversation getPrivateConversation(Integer conversationId);
    List<PrivateConversation> getUserPrivateConversations(Integer userId);
    
    // Group conversation methods
    GroupeConversation createGroupConversation(Integer ownerId, String name);
    GroupeConversation getGroupConversation(Integer conversationId);
    List<GroupeConversation> getUserGroupConversations(Integer userId);
    void addMemberToGroup(Integer groupId, Integer userId);
    void removeMemberFromGroup(Integer groupId, Integer userId);
    void makeGroupAdmin(Integer groupId, Integer userId);
    
    // Get all user conversations (both private and group)
    Stream<ConversationWithLastMessage> getUserConversations(Integer userId);
} 