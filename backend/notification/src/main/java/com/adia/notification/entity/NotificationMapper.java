package com.adia.notification.entity;

import com.adia.notification.Notification;
import com.adia.notification.NotificationProto;
import com.adia.notification.NotificationType;
import com.adia.notification.entity.NotificationEntity;
import com.adia.notification.entity.NotificationStatus;


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
                .setId(entity.getId())
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

    /**
     * Converts NotificationEntity.NotificationType to gRPC NotificationType
     */
    private NotificationType convertNotificationType(NotificationEntity.NotificationType type) {
        switch (type) {
            case GROUP_INVITE:
                return NotificationType.GROUP_INVITE;
            case GLOBAL_ANNOUNCEMENT:
                return NotificationType.GLOBAL_ANNOUNCEMENT;
            case ADMIN_WARNING:
                return NotificationType.ADMIN_WARNING;
            default:
                return NotificationType.UNRECOGNIZED;
        }
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