package com.adia.chat.repository;

import com.adia.chat.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.adia.chat.entity.GroupMemberEntity;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeMemberRepository extends JpaRepository<GroupMemberEntity, GroupMemberId> {
    List<GroupMemberEntity> findByGroupeId(Integer groupeId);
    List<GroupMemberEntity> findByUserId(Integer userId);
    boolean existsByUserIdAndGroupeId(Integer userId, Integer groupeId);
    Optional<GroupMemberEntity> findByUserIdAndGroupeId(Integer userId, Integer groupeId);
    void deleteByUserIdAndGroupeId(Integer userId, Integer groupeId);
} 