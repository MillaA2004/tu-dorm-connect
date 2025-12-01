package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.config.ChatMessageWSRequest;
import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;
import com.tuconnect.dorm_connect.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chats.sendMessage")
    public void handleSendMessage(ChatMessageWSRequest payload) {

        Long chatId = payload.chatId();
        Long userId = payload.userId();
        String content = payload.content();


        MessageDTO saved = messageService.sendMessage(chatId, userId, content);


        String destination = "/topic/chats/" + chatId;
        messagingTemplate.convertAndSend(destination, saved);
    }
}
