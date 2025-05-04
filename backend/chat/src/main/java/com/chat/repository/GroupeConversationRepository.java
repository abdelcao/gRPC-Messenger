package com.chat.repository;

import com.chat.entity.GroupeConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GroupeConversationRepository extends JpaRepository<GroupeConversation, Long> {
    Optional<GroupeConversation> findByConversationId(Long conversationId);
} 