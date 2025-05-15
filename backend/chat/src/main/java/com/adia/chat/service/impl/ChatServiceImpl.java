package com.adia.chat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adia.chat.entity.*;
import com.adia.chat.repository.*;
import com.adia.chat.service.ChatService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

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
    public Conversation createConversation(Integer ownerId) {
        Conversation conversation = new Conversation();
        conversation.setOwnerId(ownerId);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    @Override
    public Conversation getConversation(Integer id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    @Override
    @Transactional
    public Message sendMessage(Integer userId, Integer conversationId, String text) {
        Message message = new Message();
        message.setText(text);
        message.setUserId(userId);
        message.setConversationId(conversationId);
        message.setStatus(Message.MessageStatus.sent);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public Message editMessage(Integer messageId, String newText) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setText(newText);
        message.setEdited(true);
        message.setUpdatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public void updateMessageStatus(Integer messageId, Message.MessageStatus status) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setStatus(status);
        message.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Override
    public List<Message> getConversationMessages(Integer conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Override
    @Transactional
    public PrivateConversation createPrivateConversation(Integer ownerId, Integer receiverId) {
        Conversation conversation = createConversation(ownerId);
        
        PrivateConversation privateConversation = new PrivateConversation();
        privateConversation.setConversationId(conversation.getId());
        privateConversation.setReceiverId(receiverId);
        privateConversation.setCreatedAt(LocalDateTime.now());
        privateConversation.setUpdatedAt(LocalDateTime.now());
        
        return privateConversationRepository.save(privateConversation);
    }

    @Override
    public PrivateConversation getPrivateConversation(Integer conversationId) {
        return privateConversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Private conversation not found"));
    }

    @Override
    public List<PrivateConversation> getUserPrivateConversations(Integer userId) {
        // Get conversations where user is receiver
        List<PrivateConversation> receiverConversations = privateConversationRepository.findByReceiverId(userId);
        
        // Get conversations where user is owner and convert to PrivateConversation
        List<PrivateConversation> ownerConversations = conversationRepository.findByOwnerId(userId).stream()
            .map(conversation -> privateConversationRepository.findByConversationId(conversation.getId()))
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .toList();
            
        return Stream.concat(
            receiverConversations.stream(),
            ownerConversations.stream()
        ).toList();
    }

    @Override
    @Transactional
    public GroupeConversation createGroupConversation(Integer ownerId, String name) {
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
    public GroupeConversation getGroupConversation(Integer conversationId) {
        return groupeConversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Group conversation not found"));
    }


    @Override
    @Transactional
    public void addMemberToGroup(Integer groupId, Integer userId) {
        // Check if group exists
        if (!groupeConversationRepository.existsById(groupId)) {
            throw new RuntimeException("Group does not exist");
        }
        
        // If user is already a member, just return silently
        if (groupeMemberRepository.existsByUserIdAndGroupeId(userId, groupId)) {
            return;
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
    public void removeMemberFromGroup(Integer groupId, Integer userId) {
        GroupeMemberId id = new GroupeMemberId(userId, groupId);
        groupeMemberRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void makeGroupAdmin(Integer groupId, Integer userId) {
        GroupeMemberId id = new GroupeMemberId(userId, groupId);
        GroupeMember member = groupeMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group member not found"));
        member.setAdmin(true);
        groupeMemberRepository.save(member);
    }

    @Override
    public List<GroupeConversation> getUserGroupConversations(Integer userId) {
        // Get all group memberships for the user
        List<GroupeMember> groupMembers = groupeMemberRepository.findByUserId(userId);
        
        // Get all group conversations for these memberships
        List<Integer> groupIds = groupMembers.stream()
                .map(GroupeMember::getGroupeId)
                .toList();
                
        return groupeConversationRepository.findByConversationIdIn(groupIds);
    }

    @Override
    public Stream<Conversation> getUserConversations(Integer userId) {
        // Get private conversations where user is receiver or owner
        List<PrivateConversation> privateConversationsAsReceiver = privateConversationRepository.findByReceiverId(userId);
        List<Conversation> receiverConversations = privateConversationsAsReceiver.stream()
                .map(PrivateConversation::getConversationId)
                .map(conversationRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .toList();

        List<Conversation> ownerConversations = conversationRepository.findByOwnerId(userId);

        Stream<Conversation> privateConversationStream = Stream.concat(
        ownerConversations.stream(),
        receiverConversations.stream()
            
        );

        // Get group conversations where user is a member
        List<GroupeMember> groupMembers = groupeMemberRepository.findByUserId(userId);
        List<Integer> groupIds = groupMembers.stream()
                .map(GroupeMember::getGroupeId)
                .toList();
        List<GroupeConversation> groupConversations = groupeConversationRepository.findAllById(groupIds);
        Stream<Conversation> groupConversationStream = groupConversations.stream()
                .map(GroupeConversation::getConversationId)
                .map(conversationRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get);

        // Combine both streams
        return Stream.concat(privateConversationStream, groupConversationStream).distinct();
    }
} 