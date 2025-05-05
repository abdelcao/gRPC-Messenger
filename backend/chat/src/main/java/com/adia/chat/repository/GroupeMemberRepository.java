package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.GroupeMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeMemberRepository extends JpaRepository<GroupeMember, Long> {
    List<GroupeMember> findByGroupeId(Long groupeId);
    List<GroupeMember> findByUserId(Long userId);
    boolean existsByUserIdAndGroupeId(Long userId, Long groupeId);
    Optional<GroupeMember> findByUserIdAndGroupeId(Long userId, Long groupeId);
    void deleteByUserIdAndGroupeId(Long userId, Long groupeId);
} 