package com.adia.chat.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "groupe_members")
@IdClass(GroupeMemberId.class)
public class GroupeMember {
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Id
    @Column(name = "groupe_id", nullable = false)
    private Integer groupeId;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
} 