package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Chat.ChatDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {
    ChatDTO createDirectChat(Long currentUserId, Long otherUserId);

    ChatDTO createGroupChat(Long currentUserId, String name, List<Long> memberIds);

    List<ChatDTO> getChatsForUser(Long userId);

    void addMember(Long chatId, Long userId, String chatRole);

    void removeMember(Long chatId, Long userId);

    void assertUserInChat(Long userId, Long chatId);

    ChatDTO getChatById(Long chatId);
}
