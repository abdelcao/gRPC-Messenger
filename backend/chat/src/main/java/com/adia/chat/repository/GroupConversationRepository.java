package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.GroupConversationEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeConversationRepository extends JpaRepository<GroupConversationEntity, Integer> {
    Optional<GroupConversationEntity> findByConversationId(Integer conversationId);
    List<GroupConversationEntity> findByConversationIdIn(List<Integer> conversationIds);
} 