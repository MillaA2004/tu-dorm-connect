package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.notification.NotificationDTO;
import com.tuconnect.dorm_connect.mapper.NotificationMapper;
import com.tuconnect.dorm_connect.model.*;
import com.tuconnect.dorm_connect.repository.NotificationRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationPushServiceImpl notificationPushService;

    @Override
    public void notifyPostCommented(Long postAuthorId, Long commenterId, Long postId) {
        if (postAuthorId.equals(commenterId)) return;

        User recipient = userRepository.getReferenceById(postAuthorId);
        User actor = userRepository.getReferenceById(commenterId);

        Notification n = Notification.builder()
                .recipient(recipient)
                .actor(actor)
                .type(NotificationType.POST_COMMENTED)
                .targetType(TargetType.POST)
                .targetId(postId)
                .message("commented on your post")
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(n);


        NotificationDTO dto = notificationMapper.toDto(n);
        String recipientEmail = recipient.getEmail();

        notificationPushService.pushToUser(recipientEmail, dto);

        long unread = notificationRepository.countByRecipient_IdAndReadAtIsNull(recipient.getId());
        notificationPushService.pushUnreadCount(recipientEmail, unread);
    }

    @Override
    public void notifyEventJoined(Long eventCreatorId, Long joinerId, Long eventId) {
        if (eventCreatorId.equals(joinerId)) return;

        User recipient = userRepository.getReferenceById(eventCreatorId);
        User actor = userRepository.getReferenceById(joinerId);

        Notification n = Notification.builder()
                .recipient(recipient)
                .actor(actor)
                .type(NotificationType.EVENT_JOINED)
                .targetType(TargetType.EVENT)
                .targetId(eventId)
                .message("joined your event")
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(n);


        NotificationDTO dto = notificationMapper.toDto(n);
        String recipientEmail = recipient.getEmail();

        notificationPushService.pushToUser(recipientEmail, dto);

        long unread = notificationRepository.countByRecipient_IdAndReadAtIsNull(recipient.getId());
        notificationPushService.pushUnreadCount(recipientEmail, unread);
    }

    @Override
    public void markAsRead(Long notificationId, Long recipientId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!n.getRecipient().getId().equals(recipientId)) {
            throw new RuntimeException("Not allowed");
        }

        if (n.getReadAt() == null) {
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);


            String email = n.getRecipient().getEmail();
            long unread = notificationRepository.countByRecipient_IdAndReadAtIsNull(recipientId);
            notificationPushService.pushUnreadCount(email, unread);
        }
    }

    @Override
    public long unreadCount(Long recipientId) {
        return notificationRepository.countByRecipient_IdAndReadAtIsNull(recipientId);
    }

    public List<NotificationDTO> getMyNotifications(Long userId, int page, int size) {
        return notificationRepository
                .findLatestByRecipientWithActor(userId, PageRequest.of(page, size))
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipient_IdAndReadAtIsNull(userId);
    }
}

