package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.PrivateConversation;

import java.util.Optional;

@Repository
public interface PrivateConversationRepository extends JpaRepository<PrivateConversation, Long> {
    Optional<PrivateConversation> findByConversationId(Long conversationId);
    Optional<PrivateConversation> findByConversationIdAndReceiverId(Long conversationId, Long receiverId);
} 