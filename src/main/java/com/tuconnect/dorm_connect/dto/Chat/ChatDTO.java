package com.tuconnect.dorm_connect.dto.Chat;

import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;

import java.util.List;

public record ChatDTO(
        Long chatId,
        String name,
        boolean groupChat,
        List<ChatMemberDTO> members,
        MessageDTO lastMessage
) {
}
