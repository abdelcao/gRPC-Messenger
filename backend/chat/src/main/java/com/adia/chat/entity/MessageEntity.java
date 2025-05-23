package com.adia.chat.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String text;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "conversation_id", nullable = false)
    private Integer conversationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.sent;

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum MessageStatus {
        sent, delivered, read
    }
} 