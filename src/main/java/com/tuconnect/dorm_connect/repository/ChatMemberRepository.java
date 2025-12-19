package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    List<ChatMember> findByChatChatId(Long chatId);

    boolean existsByChatChatIdAndUserId(Long chatId, Long userId);

    Optional<ChatMember> findByChatChatIdAndUserId(Long chatId, Long userId);

    List<ChatMember> findAllByChatChatId(Long chatId);

    void deleteByChatChatId(Long chatId);
}
