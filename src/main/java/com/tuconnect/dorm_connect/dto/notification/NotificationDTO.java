package com.tuconnect.dorm_connect.dto.notification;

import com.tuconnect.dorm_connect.model.NotificationType;
import com.tuconnect.dorm_connect.model.TargetType;

import java.time.LocalDateTime;

public record NotificationDTO(

        Long id,
        NotificationType type,
        TargetType targetType,
        Long targetId,
        String message,
        boolean read,
        LocalDateTime createdAt,
        Long actorId,
        String actorName,
        String actorImageUrl
) {
}
