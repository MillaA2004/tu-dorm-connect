package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;
import com.tuconnect.dorm_connect.dto.Messages.SendMessageRequest;
import com.tuconnect.dorm_connect.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats/{chatId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @GetMapping
    public Page<MessageDTO> getMessages(@PathVariable Long chatId,
                                        @RequestParam Long userId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "50") int size) {


        return messageService.getMessages(chatId, userId, page, size);
    }


    @PostMapping
    public MessageDTO sendMessage(@PathVariable Long chatId,
                                  @RequestParam Long userId,
                                  @Valid @RequestBody SendMessageRequest request) {



        return messageService.sendMessage(chatId, userId, request.content());
    }
}
