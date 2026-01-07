package com.tuconnect.dorm_connect.dto.Messages;

import java.time.Instant;



public record MessageDTO(
        Long messageId,
        Long chatId,
        Long userId,
        String senderName,
        String senderImageUrl,
        String content,
        Instant sentAt
) {}

