package com.tuconnect.dorm_connect.config;

public record ChatMessageWSRequest(
        Long chatId,
        Long userId,
        String content
) {
}
