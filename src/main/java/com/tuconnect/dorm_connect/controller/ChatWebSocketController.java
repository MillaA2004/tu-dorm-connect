package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.config.ChatMessageWSRequest;
import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;
import com.tuconnect.dorm_connect.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

//@Controller
//@RequiredArgsConstructor
//public class ChatWebSocketController {
//
//    private final MessageService messageService;
//    private final SimpMessagingTemplate messagingTemplate;
//
//
//    @MessageMapping("/chats.sendMessage")
//    public void handleSendMessage(ChatMessageWSRequest payload) {
//
//        Long chatId = payload.chatId();
//        Long userId = payload.userId();
//        String content = payload.content();
//
//
//        MessageDTO saved = messageService.sendMessage(chatId, userId, content);
//
//
//        String destination = "/topic/chats/" + chatId;
//        messagingTemplate.convertAndSend(destination, saved);
//    }
//}

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats.sendMessage")
    public void handleSendMessage(ChatMessageWSRequest payload, Principal principal) {

        if (principal == null) {
            throw new IllegalStateException("Unauthenticated WebSocket connection");
        }

        Long chatId = payload.chatId();
        String content = payload.content();


        String email = principal.getName();

        MessageDTO saved = messageService.sendMessage(chatId, email, content);

        messagingTemplate.convertAndSend("/topic/chats/" + chatId, saved);
    }
}
