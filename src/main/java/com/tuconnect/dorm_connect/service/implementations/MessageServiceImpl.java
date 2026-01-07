package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;
import com.tuconnect.dorm_connect.mapper.MessageMapper;
import com.tuconnect.dorm_connect.model.Chat;
import com.tuconnect.dorm_connect.model.Message;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.ChatRepository;
import com.tuconnect.dorm_connect.repository.MessageRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.ChatService;
import com.tuconnect.dorm_connect.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;



@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(
            MessageRepository messageRepository,
            ChatRepository chatRepository,
            UserRepository userRepository,
            ChatService chatService,
            MessageMapper messageMapper
    ) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.messageMapper = messageMapper;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public MessageDTO sendMessage(Long chatId, String senderEmail, String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        User sender = getUserByEmail(senderEmail);
        chatService.assertUserInChat(sender.getId(), chatId);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(Instant.now());

        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);
    }

    @Override
    public Page<MessageDTO> getMessages(
            Long chatId,
            String requesterEmail,
            int page,
            int size
    ) {
        User requester = getUserByEmail(requesterEmail);
        chatService.assertUserInChat(requester.getId(), chatId);

        PageRequest pageable = PageRequest.of(page, size);

        return messageRepository
                .findByChatChatIdOrderBySentAtAsc(chatId, pageable)
                .map(messageMapper::toDto);
    }

    @Override
    public MessageDTO editMessage(Long chatId, Long messageId, String requesterEmail, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        User requester = getUserByEmail(requesterEmail);
        chatService.assertUserInChat(requester.getId(), chatId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));


        if (!message.getChat().getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }


        if (!message.getSender().getId().equals(requester.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You can edit only your own messages");
        }

        message.setContent(content.trim());

        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);
    }
}
