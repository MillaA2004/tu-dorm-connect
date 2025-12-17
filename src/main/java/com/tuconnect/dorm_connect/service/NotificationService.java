package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.notification.NotificationDTO;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface NotificationService {

    void notifyPostCommented(Long postAuthorId, Long commenterId, Long postId);
    void notifyEventJoined(Long eventCreatorId, Long joinerId, Long eventId);

    void markAsRead(Long notificationId, Long recipientId);
    long unreadCount(Long recipientId);

    List<NotificationDTO> getMyNotifications(Long userId, int page, int size);
    long getUnreadCount(Long userId);
}
