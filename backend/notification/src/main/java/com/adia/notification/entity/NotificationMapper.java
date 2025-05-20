package com.adia.notification.entity;

import com.adia.notification.Notification;
import com.adia.notification.NotificationType;

import java.time.Instant;

import org.springframework.stereotype.Component;
import com.google.protobuf.Timestamp;

/**
 * Mapper class to convert between NotificationEntity and gRPC Notification objects
 */
@Component
public class NotificationMapper {

    /**
     * Converts a NotificationEntity to a gRPC Notification message
     *
     * @param entity the NotificationEntity to convert
     * @return the gRPC Notification message
     */
    public Notification toGrpc(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return Notification.newBuilder()
                .setId(String.valueOf(entity.getId()))
                .setReceiverId(entity.getReceiverId())
                .setSenderId(entity.getSenderId())
                .setContent(entity.getContent())
                .setType(convertNotificationType(entity.getType()))
                .setTitle(entity.getTitle())
                .setLink(entity.getLink() != null ? entity.getLink() : "")
                .setCreatedAt(instantToTimestamp(entity.getCreatedAt()))
                .setUnread(entity.isUnread())
                .build();
    }

    public NotificationEntity toEntity(Notification notification) {
        return NotificationEntity.builder()
                .id(Long.parseLong(notification.getId()))
                .receiverId(notification.getReceiverId())
                .senderId(notification.getSenderId())
                .content(notification.getContent())
                .type(convertNotificationType(notification.getType()))
                .title(notification.getTitle())
                .link(notification.getLink())
                .createdAt(timestampToInstant(notification.getCreatedAt()))
                .unread(notification.getUnread())
                .build();
    }

    /**
     * Converts NotificationEntity.NotificationType to gRPC NotificationType
     */
    private NotificationType convertNotificationType(NotificationEntity.NotificationType type) {
        return switch (type) {
            case GROUP_INVITE -> NotificationType.GROUP_INVITE;
            case GLOBAL_ANNOUNCEMENT -> NotificationType.GLOBAL_ANNOUNCEMENT;
            case ADMIN_WARNING -> NotificationType.ADMIN_WARNING;
            default -> NotificationType.UNRECOGNIZED;
        };
    }

    /**
     * Converts NotificationType to NotificationEntity.NotificationType
     */
    private NotificationEntity.NotificationType convertNotificationType(NotificationType type) {
        return switch (type) {
            case GROUP_INVITE -> NotificationEntity.NotificationType.GROUP_INVITE;
            case GLOBAL_ANNOUNCEMENT -> NotificationEntity.NotificationType.GLOBAL_ANNOUNCEMENT;
            case ADMIN_WARNING -> NotificationEntity.NotificationType.ADMIN_WARNING;
            default -> NotificationEntity.NotificationType.UNRECOGNIZED;
        };
    }

    /**
     * Converts Java Instant to Protobuf Timestamp
     */
    private Timestamp instantToTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    /**
     * Converts Protobuf Timestamp to Java Instant
     */
    private Instant timestampToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}