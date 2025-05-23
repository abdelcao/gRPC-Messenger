package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.PrivateConversationEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateConversationRepository extends JpaRepository<PrivateConversationEntity, Long> {
    // Get all PRIVATE conversations where user is either owner or receiver
    @Query(value = """
        SELECT DISTINCT c.* FROM conversations c
        JOIN private_conversations pc ON pc.conversation_id = c.id
        WHERE c.owner_id = :userId
           OR pc.receiver_id = :userId
        """, nativeQuery = true)
    List<PrivateConversationEntity> findPrivateConversationsByUser(@Param("userId") Long userId);
} 