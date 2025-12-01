package com.tuconnect.dorm_connect.dto.Chat;

public record ChatMemberDTO(
        Long chatMemberId,
        Long userId,
        String firstName,
        String lastName,
        String chatRole
) {
}
