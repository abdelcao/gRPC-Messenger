package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.Conversation;
import java.util.List;
 
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    List<Conversation> findByOwnerId(Integer ownerId);
} 