package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.Conversation;
 
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
} 