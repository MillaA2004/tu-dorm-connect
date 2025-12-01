package com.tuconnect.dorm_connect.service.ServiceImpl;

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

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,ChatRepository chatRepository,UserRepository userRepository,ChatService chatService,MessageMapper messageMapper) {
        this.chatRepository=chatRepository;
        this.messageRepository = messageRepository;
        this.messageMapper=messageMapper;
        this.chatService=chatService;
        this.userRepository=userRepository;
    }


    public MessageDTO sendMessage(Long chatId, Long senderId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }


        chatService.assertUserInChat(senderId, chatId);


        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found: " + chatId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + senderId));


        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(Instant.now());

        Message saved = messageRepository.save(message);


        return messageMapper.toDto(saved);
    }



    public Page<MessageDTO> getMessages(Long chatId, Long requesterId, int page, int size) {

        chatService.assertUserInChat(requesterId, chatId);


        PageRequest pageable = PageRequest.of(page, size);
        Page<Message> messagesPage =
                messageRepository.findByChatChatIdOrderBySentAtAsc(chatId, pageable);


        return messagesPage.map(messageMapper::toDto);
    }
}
