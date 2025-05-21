package com.adia.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adia.chat.entity.GroupeMember;
import com.adia.chat.entity.GroupeMemberId;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeMemberRepository extends JpaRepository<GroupeMember, GroupeMemberId> {
    List<GroupeMember> findByGroupeId(Integer groupeId);
    List<GroupeMember> findByUserId(Integer userId);
    boolean existsByUserIdAndGroupeId(Integer userId, Integer groupeId);
    Optional<GroupeMember> findByUserIdAndGroupeId(Integer userId, Integer groupeId);
    void deleteByUserIdAndGroupeId(Integer userId, Integer groupeId);
} 