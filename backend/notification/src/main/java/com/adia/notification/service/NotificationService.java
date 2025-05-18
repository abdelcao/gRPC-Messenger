package com.adia.notification.service;

import com.adia.notification.entity.NotificationEntity;
import com.adia.notification.entity.NotificationStatus;
import com.adia.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationEntity createNotification(Long userId, String content, String link) {
        NotificationEntity notif = NotificationEntity.builder()
                .userId(userId)
                .content(content)
                .link(link != null ? link : "")
                .status(NotificationStatus.UNREAD)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return repository.save(notif);

    }

    public List<NotificationEntity> getNotificationsForUser(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }


    public void markAsRead(Long notificationId) {
        repository.findById(notificationId).ifPresent(notif -> {
            notif.setStatus(NotificationStatus.READ);
            repository.save(notif);
        });
    }


    public void markAllAsRead(Long userId) {
        List<NotificationEntity> notifs = repository.findByUserIdAndStatus(userId, NotificationStatus.UNREAD);
        notifs.forEach(notif -> notif.setStatus(NotificationStatus.READ));
        repository.saveAll(notifs);
    }


    public void dismissNotification(Long notificationId) {
        repository.findById(notificationId).ifPresent(notif -> {
            notif.setStatus(NotificationStatus.DISMISSED);
            repository.save(notif);
        });
    }
}
