package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {


    Optional<Message> findFirstByChatChatIdOrderBySentAtDesc(Long chatId);

    Page<Message> findByChatChatIdOrderBySentAtAsc(Long chatId, Pageable pageable);

    long countByChatChatIdAndSentAtAfterAndSender_IdNot(Long chatId, Instant after, Long userId);


}

