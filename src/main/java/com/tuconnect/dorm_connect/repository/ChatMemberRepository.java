package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    List<ChatMember> findByChatChatId(Long chatId);




    boolean existsByChatChatIdAndUserUserId(Long chatId, Long userId);

    Optional<ChatMember> findByChatChatIdAndUserUserId(Long chatId, Long userId);
}
