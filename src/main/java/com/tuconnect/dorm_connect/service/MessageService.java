package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    MessageDTO sendMessage(Long chatId, String senderEmail, String content);

    Page<MessageDTO> getMessages(Long chatId, String requesterEmail, int page, int size);

    MessageDTO editMessage(Long chatId, Long messageId, String requesterEmail, String content);
}
