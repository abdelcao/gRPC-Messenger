package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.Message;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);
    List<Message> findByUserIdAndConversationId(Integer userId, Integer conversationId);
    Optional<Message> findFirstByConversationIdOrderByCreatedAtDesc(Integer conversationId);
} 