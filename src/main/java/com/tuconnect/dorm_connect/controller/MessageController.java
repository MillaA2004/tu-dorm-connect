package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Messages.EditMessageRequest;
import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;
import com.tuconnect.dorm_connect.dto.Messages.SendMessageRequest;
import com.tuconnect.dorm_connect.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/chats/{chatId}/messages")
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService,SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate=messagingTemplate;
    }


    @GetMapping
    public Page<MessageDTO> getMessages(
            @PathVariable Long chatId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return messageService.getMessages(
                chatId,
                authentication.getName(),
                page,
                size
        );
    }




    @PostMapping
    public MessageDTO sendMessage(
            @PathVariable Long chatId,
            Authentication authentication,
            @Valid @RequestBody SendMessageRequest request
    ) {
        MessageDTO saved = messageService.sendMessage(
                chatId,
                authentication.getName(),
                request.content()
        );


        messagingTemplate.convertAndSend("/topic/chats/" + chatId, saved);

        return saved;
    }


    @PutMapping("/{messageId}")
    public MessageDTO editMessage(
            @PathVariable Long chatId,
            @PathVariable Long messageId,
            Authentication authentication,
            @Valid @RequestBody EditMessageRequest request
    ) {
        return messageService.editMessage(
                chatId,
                messageId,
                authentication.getName(),
                request.content()
        );
    }
}

