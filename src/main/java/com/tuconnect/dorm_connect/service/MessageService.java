package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    MessageDTO sendMessage(Long chatId, Long senderId, String content);

    Page<MessageDTO> getMessages(Long chatId, Long requesterId, int page, int size);
}
