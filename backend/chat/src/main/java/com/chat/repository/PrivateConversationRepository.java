package com.chat.repository;

import com.chat.entity.PrivateConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PrivateConversationRepository extends JpaRepository<PrivateConversation, Long> {
    Optional<PrivateConversation> findByConversationId(Long conversationId);
    Optional<PrivateConversation> findByConversationIdAndReceiverId(Long conversationId, Long receiverId);
} 