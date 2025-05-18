package com.adia.chat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adia.chat.entity.*;
import com.adia.chat.repository.*;
import com.adia.chat.service.ChatService;
import com.adia.chat.grpc.ChatGrpcService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

@Service
public class ChatServiceImpl implements ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    
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
        Message savedMessage = messageRepository.save(message);

        // Broadcast updated conversation to all relevant users
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        ConversationWithLastMessage convWithLastMsg = new ConversationWithLastMessage(conversation, savedMessage);
        com.adia.chat.grpc.Conversation grpcConv = com.adia.chat.grpc.Conversation.newBuilder()
            .setId(conversation.getId())
            .setOwnerId(conversation.getOwnerId())
            .setCreatedAt(conversation.getCreatedAt().toString())
            .setUpdatedAt(conversation.getUpdatedAt().toString())
            .setLastMessage(
                com.adia.chat.grpc.Message.newBuilder()
                    .setId(savedMessage.getId())
                    .setUserId(savedMessage.getUserId())
                    .setConversationId(savedMessage.getConversationId())
                    .setText(savedMessage.getText())
                    .setEdited(savedMessage.isEdited())
                    .setStatus(com.adia.chat.grpc.MessageStatus.valueOf(savedMessage.getStatus().name().toUpperCase()))
                    .setCreatedAt(savedMessage.getCreatedAt().toString())
                    .setUpdatedAt(savedMessage.getUpdatedAt().toString())
                    .build()
            )
            .build();
        // Owner
        ChatGrpcService.broadcastConversationUpdate(conversation.getOwnerId(), grpcConv);
        // For private conversations, also notify the receiver
        privateConversationRepository.findByConversationId(conversationId).ifPresent(pc -> {
            ChatGrpcService.broadcastConversationUpdate(pc.getReceiverId(), grpcConv);
        });
        // For group conversations, notify all members
        groupeConversationRepository.findByConversationId(conversationId).ifPresent(gc -> {
            List<GroupeMember> members = groupeMemberRepository.findByGroupeId(gc.getId());
            for (GroupeMember member : members) {
                ChatGrpcService.broadcastConversationUpdate(member.getUserId(), grpcConv);
            }
        });
        return savedMessage;
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
                
        return groupeConversationRepository.findAllById(groupIds);
    }

    @Override
    public Stream<ConversationWithLastMessage> getUserConversations(Integer userId) {
        logger.debug("Getting conversations for user: {}", userId);
        
        // Get private conversations where user is receiver or owner
        List<PrivateConversation> privateConversationsAsReceiver = privateConversationRepository.findByReceiverId(userId);
        logger.debug("Found {} private conversations where user is receiver", privateConversationsAsReceiver.size());
        
        Stream<ConversationWithLastMessage> receiverConversations = privateConversationsAsReceiver.stream()
                .map(PrivateConversation::getConversationId)
                .map(conversationRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(conversation -> {
                    Message lastMessage = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId())
                            .orElse(null);
                    logger.debug("Conversation {} last message: {}", conversation.getId(), 
                        lastMessage != null ? lastMessage.getId() : "null");
                    return new ConversationWithLastMessage(conversation, lastMessage);
                });

        List<Conversation> ownerConversations = conversationRepository.findByOwnerId(userId);
        logger.debug("Found {} conversations where user is owner", ownerConversations.size());
        
        Stream<ConversationWithLastMessage> ownerConversationStream = ownerConversations.stream()
                .map(conversation -> {
                    Message lastMessage = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId())
                            .orElse(null);
                    logger.debug("Conversation {} last message: {}", conversation.getId(), 
                        lastMessage != null ? lastMessage.getId() : "null");
                    return new ConversationWithLastMessage(conversation, lastMessage);
                });

        Stream<ConversationWithLastMessage> privateConversationStream = Stream.concat(
            ownerConversationStream,
            receiverConversations
        );

        // Get group conversations where user is a member
        List<GroupeMember> groupMembers = groupeMemberRepository.findByUserId(userId);
        logger.debug("Found {} group memberships for user", groupMembers.size());
        
        List<Integer> groupIds = groupMembers.stream()
                .map(GroupeMember::getGroupeId)
                .toList();
        List<GroupeConversation> groupConversations = groupeConversationRepository.findAllById(groupIds);
        logger.debug("Found {} group conversations", groupConversations.size());
        
        Stream<ConversationWithLastMessage> groupConversationStream = groupConversations.stream()
                .map(GroupeConversation::getConversationId)
                .map(conversationRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(conversation -> {
                    Message lastMessage = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId())
                            .orElse(null);
                    logger.debug("Group conversation {} last message: {}", conversation.getId(), 
                        lastMessage != null ? lastMessage.getId() : "null");
                    return new ConversationWithLastMessage(conversation, lastMessage);
                });

        // Combine both streams and ensure uniqueness
        return Stream.concat(privateConversationStream, groupConversationStream)
                .distinct();
    }
} 