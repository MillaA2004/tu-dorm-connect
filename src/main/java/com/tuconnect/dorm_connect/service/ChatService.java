package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Chat.ChatDTO;
import com.tuconnect.dorm_connect.dto.Chat.ChatMemberDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    List<ChatDTO> getChatsForCurrentUser(String email);

    ChatDTO getChatForCurrentUser(Long chatId, String email);

    ChatDTO createDirectChat(String email, Long otherUserId);

    ChatDTO createGroupChat(String email, String name, List<Long> memberIds);

    void addMember(Long chatId, String requesterEmail, Long newMemberId, String role);

    void removeMember(Long chatId, String requesterEmail, Long memberId);

    void assertUserInChat(Long userId, Long chatId);

    List<ChatMemberDTO> getChatMembers(Long chatId, String email);

    void markAsRead(Long chatId, String email);

    void deleteDirectChatIfEmpty(Long chatId, String email);
}
