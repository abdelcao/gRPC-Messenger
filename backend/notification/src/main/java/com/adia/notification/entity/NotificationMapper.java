package com.adia.notification.entity;

import com.adia.notification.Notification;
import com.adia.notification.entity.NotificationEntity;
import com.adia.notification.entity.NotificationStatus;

public class NotificationMapper {

    public static Notification toGrpc(NotificationEntity entity) {
        return Notification.newBuilder()
                .setId(entity.getId().toString())
                .setUsername(entity.getUserId().toString())
                .setContent(entity.getContent())
                .setCreatedAt(entity.getCreatedAt().toString())
                .setUnread(entity.getStatus() == NotificationStatus.UNREAD)
                .setLink(entity.getLink() != null ? entity.getLink() : "")
                .build();
    }
}
