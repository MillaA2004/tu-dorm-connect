package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Chat.*;
import com.tuconnect.dorm_connect.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;




@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @GetMapping
    public List<ChatDTO> getMyChats(Authentication authentication) {
        return chatService.getChatsForCurrentUser(authentication.getName());
    }


    @GetMapping("/{chatId}")
    public ChatDTO getChatById(@PathVariable Long chatId,
                               Authentication authentication) {
        return chatService.getChatForCurrentUser(chatId, authentication.getName());
    }


    @PostMapping("/direct")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatDTO createDirectChat(
            @Valid @RequestBody CreateDirectChatRequest request,
            Authentication authentication
    ) {
        return chatService.createDirectChat(
                authentication.getName(),
                request.otherUserId()
        );
    }


    @PostMapping("/group")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatDTO createGroupChat(
            @Valid @RequestBody CreateGroupChatRequest request,
            Authentication authentication
    ) {
        return chatService.createGroupChat(
                authentication.getName(),
                request.name(),
                request.memberIds()
        );
    }


    @PostMapping("/{chatId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMember(@PathVariable Long chatId,
                          @Valid @RequestBody AddMemberRequest request,
                          Authentication authentication) {

        chatService.addMember(
                chatId,
                authentication.getName(),
                request.userId(),
                request.chatRole()
        );
    }


    @DeleteMapping("/{chatId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Long chatId,
                             @PathVariable Long memberId,
                             Authentication authentication) {

        chatService.removeMember(
                chatId,
                authentication.getName(),
                memberId
        );
    }


    @GetMapping("/{chatId}/members")
    public List<ChatMemberDTO> getMembers(@PathVariable Long chatId, Principal principal) {
        return chatService.getChatMembers(chatId, principal.getName());
    }

    @PostMapping("/{chatId}/read")
    public void markAsRead(@PathVariable Long chatId, Principal principal) {
        chatService.markAsRead(chatId, principal.getName());
    }

    @DeleteMapping("/{chatId}/if-empty")
    public ResponseEntity<Void> deleteIfEmpty(@PathVariable Long chatId, Principal principal) {
        chatService.deleteDirectChatIfEmpty(chatId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}

