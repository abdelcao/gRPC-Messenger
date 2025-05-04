package com.chat.service.impl;

import com.chat.entity.*;
import com.chat.repository.*;
import com.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private PrivateConversationRepository privateConversationRepository;
    
    @Autowired
    private GroupeConversationRepository groupeConversationRepository;
    
    @Autowired
    private GroupeMemberRepository groupeMemberRepository;

    @Override
    @Transactional
    public Conversation createConversation(Long ownerId) {
        Conversation conversation = new Conversation();
        conversation.setOwnerId(ownerId);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    @Override
    public Conversation getConversation(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    @Override
    @Transactional
    public Message sendMessage(Long userId, Long conversationId, String text) {
        Message message = new Message();
        message.setText(text);
        message.setUserId(userId);
        message.setConversationId(conversationId);
        message.setStatus(Message.MessageStatus.SENT);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public Message editMessage(Long messageId, String newText) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setText(newText);
        message.setEdited(true);
        message.setUpdatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public void updateMessageStatus(Long messageId, Message.MessageStatus status) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setStatus(status);
        message.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Override
    public List<Message> getConversationMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Override
    @Transactional
    public PrivateConversation createPrivateConversation(Long ownerId, Long receiverId) {
        Conversation conversation = createConversation(ownerId);
        
        PrivateConversation privateConversation = new PrivateConversation();
        privateConversation.setConversationId(conversation.getId());
        privateConversation.setReceiverId(receiverId);
        privateConversation.setCreatedAt(LocalDateTime.now());
        privateConversation.setUpdatedAt(LocalDateTime.now());
        
        return privateConversationRepository.save(privateConversation);
    }

    @Override
    public PrivateConversation getPrivateConversation(Long conversationId) {
        return privateConversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Private conversation not found"));
    }

    @Override
    @Transactional
    public GroupeConversation createGroupConversation(Long ownerId, String name) {
        Conversation conversation = createConversation(ownerId);
        
        GroupeConversation groupeConversation = new GroupeConversation();
        groupeConversation.setConversationId(conversation.getId());
        groupeConversation.setName(name);
        groupeConversation.setCreatedAt(LocalDateTime.now());
        groupeConversation.setUpdatedAt(LocalDateTime.now());
        
        GroupeConversation savedGroup = groupeConversationRepository.save(groupeConversation);
        
        // Add owner as admin member
        GroupeMember ownerMember = new GroupeMember();
        ownerMember.setUserId(ownerId);
        ownerMember.setGroupeId(savedGroup.getId());
        ownerMember.setAdmin(true);
        ownerMember.setCreatedAt(LocalDateTime.now());
        groupeMemberRepository.save(ownerMember);
        
        return savedGroup;
    }

    @Override
    public GroupeConversation getGroupConversation(Long conversationId) {
        return groupeConversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Group conversation not found"));
    }

    @Override
    @Transactional
    public void addMemberToGroup(Long groupId, Long userId) {
        if (groupeMemberRepository.existsByUserIdAndGroupeId(userId, groupId)) {
            throw new RuntimeException("User is already a member of this group");
        }
        
        GroupeMember member = new GroupeMember();
        member.setUserId(userId);
        member.setGroupeId(groupId);
        member.setAdmin(false);
        member.setCreatedAt(LocalDateTime.now());
        groupeMemberRepository.save(member);
    }

    @Override
    @Transactional
    public void removeMemberFromGroup(Long groupId, Long userId) {
        groupeMemberRepository.deleteByUserIdAndGroupeId(userId, groupId);
    }

    @Override
    @Transactional
    public void makeGroupAdmin(Long groupId, Long userId) {
        GroupeMember member = groupeMemberRepository.findByUserIdAndGroupeId(userId, groupId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        member.setAdmin(true);
        groupeMemberRepository.save(member);
    }
} 