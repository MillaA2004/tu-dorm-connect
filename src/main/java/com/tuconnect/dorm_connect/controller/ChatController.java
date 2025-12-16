package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Chat.*;
import com.tuconnect.dorm_connect.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

//@RestController
//@RequestMapping("/api/chats")
//public class ChatController {
//
//    private final ChatService chatService;
//
//    @Autowired
//    public ChatController(ChatService chatService) {
//        this.chatService = chatService;
//    }
//
//
//    @GetMapping
//    public List<ChatDTO> getChatsForUser(@RequestParam Long userId) {
//        return chatService.getChatsForUser(userId);
//    }
//
//
//    @GetMapping("/{chatId}")
//    public ChatDTO getChatById(@PathVariable Long chatId,
//                               @RequestParam Long userId) {
//        chatService.assertUserInChat(userId, chatId);
//        return chatService.getChatById(chatId);
//    }
//
//
//    @PostMapping("/direct")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ChatDTO createDirectChat(@Valid @RequestBody CreateDirectChatRequest request) {
//        return chatService.createDirectChat(request.currentUserId(), request.otherUserId());
//    }
//
//
//    @PostMapping("/group")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ChatDTO createGroupChat(@Valid @RequestBody CreateGroupChatRequest request) {
//        return chatService.createGroupChat(
//                request.currentUserId(),
//                request.name(),
//                request.memberIds()
//        );
//    }
//
//
//    @PostMapping("/{chatId}/members")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void addMember(@PathVariable Long chatId,
//                          @Valid @RequestBody AddMemberRequest request) {
//        chatService.addMember(chatId, request.userId(), request.chatRole());
//    }
//
//
//    @DeleteMapping("/{chatId}/members/{userId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void removeMember(@PathVariable Long chatId,
//                             @PathVariable Long userId) {
//        chatService.removeMember(chatId, userId);
//    }
//}
//


@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // GET /api/chats  → my chats
    @GetMapping
    public List<ChatDTO> getMyChats(Authentication authentication) {
        return chatService.getChatsForCurrentUser(authentication.getName());
    }

    // GET /api/chats/{chatId}
    @GetMapping("/{chatId}")
    public ChatDTO getChatById(@PathVariable Long chatId,
                               Authentication authentication) {
        return chatService.getChatForCurrentUser(chatId, authentication.getName());
    }

    // POST /api/chats/direct
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

    // POST /api/chats/group
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

    // POST /api/chats/{chatId}/members
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

    // DELETE /api/chats/{chatId}/members/{memberId}
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
}

