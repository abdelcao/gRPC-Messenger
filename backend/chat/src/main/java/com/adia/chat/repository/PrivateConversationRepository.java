package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.PrivateConversation;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateConversationRepository extends JpaRepository<PrivateConversation, Integer> {
    Optional<PrivateConversation> findByConversationId(Integer conversationId);
    Optional<PrivateConversation> findByConversationIdAndReceiverId(Integer conversationId, Integer receiverId);
    List<PrivateConversation> findByReceiverId(Integer receiverId);
} 