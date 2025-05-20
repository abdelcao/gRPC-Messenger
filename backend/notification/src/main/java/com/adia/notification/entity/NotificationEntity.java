package com.adia.notification.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for notifications
 */
@Entity
@Table(name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_receiver_id", columnList = "receiver_id"),
                @Index(name = "idx_notifications_unread", columnList = "receiver_id, unread"),
                @Index(name = "idx_notifications_created_at", columnList = "created_at DESC")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    private String id;

    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(length = 2048)
    private String link;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean unread = true;

    /**
     * Enum representing different notification types
     */
    public enum NotificationType {
        GROUP_INVITE,
        ADMIN_WARNING,
        GLOBAL_ANNOUNCEMENT
    }
}

