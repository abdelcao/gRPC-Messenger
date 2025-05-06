package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.GroupeConversation;

import java.util.Optional;

@Repository
public interface GroupeConversationRepository extends JpaRepository<GroupeConversation, Integer> {
    Optional<GroupeConversation> findByConversationId(Integer conversationId);
} 