package com.tuconnect.dorm_connect.service.implementations;
import com.tuconnect.dorm_connect.dto.notification.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPushServiceImpl {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushToUser(String email, NotificationDTO dto) {

        messagingTemplate.convertAndSendToUser(email, "/queue/notifications", dto);
    }

    public void pushUnreadCount(String email, long unreadCount) {
        messagingTemplate.convertAndSendToUser(email, "/queue/notifications.unread",
                java.util.Map.of("unreadCount", unreadCount));
    }
}
