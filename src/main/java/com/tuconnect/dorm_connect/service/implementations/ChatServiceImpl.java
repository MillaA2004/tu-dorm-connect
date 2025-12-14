package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Chat.ChatDTO;
import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;
import com.tuconnect.dorm_connect.mapper.ChatMapper;
import com.tuconnect.dorm_connect.mapper.MessageMapper;
import com.tuconnect.dorm_connect.model.Chat;
import com.tuconnect.dorm_connect.model.ChatMember;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.ChatMemberRepository;
import com.tuconnect.dorm_connect.repository.ChatRepository;
import com.tuconnect.dorm_connect.repository.MessageRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.ChatService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
//public class ChatServiceImpl implements ChatService {
//
//    private final ChatRepository chatRepository;
//    private final ChatMemberRepository chatMemberRepository;
//    private final UserRepository userRepository;
//    private final MessageRepository messageRepository;
//    private final ChatMapper chatMapper;
//    private final MessageMapper messageMapper;
//
//    @Autowired
//    public ChatServiceImpl(ChatRepository chatRepository, ChatMemberRepository chatMemberRepository, UserRepository userRepository, MessageRepository messageRepository, ChatMapper chatMapper, MessageMapper messageMapper) {
//        this.chatMapper = chatMapper;
//        this.chatRepository = chatRepository;
//        this.chatMemberRepository = chatMemberRepository;
//        this.userRepository = userRepository;
//        this.messageMapper = messageMapper;
//        this.messageRepository = messageRepository;
//    }
//
//    private void addMemberInternal(Chat chat, Long userId, String chatRole) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
//
//        ChatMember member = new ChatMember();
//        member.setChat(chat);
//        member.setUser(user);
//        member.setChatRole(chatRole);
//
//        chatMemberRepository.save(member);
//    }
//
//    private ChatDTO toDtoWithLastMessage(Chat chat) {
//        ChatDTO dto = chatMapper.toDto(chat);
//
//        MessageDTO lastMessage = messageRepository
//                .findFirstByChatChatIdOrderBySentAtDesc(chat.getChatId())
//                .map(messageMapper::toDto)
//                .orElse(null);
//
//        return new ChatDTO(
//                dto.chatId(),
//                dto.name(),
//                dto.groupChat(),
//                dto.members(),
//                lastMessage
//        );
//    }
//
//
//    public ChatDTO getChatById(Long chatId) {
//        Chat chat = chatRepository.findById(chatId)
//                .orElseThrow(() -> new EntityNotFoundException("Chat not found: " + chatId));
//
//        return toDtoWithLastMessage(chat);
//    }
//
//
//    public ChatDTO createDirectChat(Long currentUserId, Long otherUserId) {
//        if (currentUserId.equals(otherUserId)) {
//            throw new IllegalArgumentException("Cannot create direct chat with yourself");
//        }
//
//
//        Chat chat = chatRepository
//                .findDirectChatBetweenUsers(currentUserId, otherUserId)
//                .orElseGet(() -> {
//                    Chat newChat = new Chat();
//                    newChat.setGroupChat(false);
//                    newChat.setName(null);
//
//                    Chat savedChat = chatRepository.save(newChat);
//
//                    addMemberInternal(savedChat, currentUserId, "Member");
//                    addMemberInternal(savedChat, otherUserId, "Member");
//
//                    return savedChat;
//                });
//
//        return toDtoWithLastMessage(chat);
//    }
//
//
//    public ChatDTO createGroupChat(Long currentUserId, String name, List<Long> memberIds) {
//        if (name == null || name.isBlank()) {
//            throw new IllegalArgumentException("Group chat name is required");
//        }
//
//        Chat chat = new Chat();
//        chat.setGroupChat(true);
//        chat.setName(name);
//
//        Chat savedChat = chatRepository.save(chat);
//
//
//        addMemberInternal(savedChat, currentUserId, "Admin");
//
//
//        if (memberIds != null) {
//            for (Long memberId : memberIds) {
//                if (!memberId.equals(currentUserId)) {
//                    addMemberInternal(savedChat, memberId, "Member");
//                }
//            }
//        }
//
//        return toDtoWithLastMessage(savedChat);
//    }
//
//
//    public void addMember(Long chatId, Long userId, String chatRole) {
//        boolean exists = chatMemberRepository.existsByChatChatIdAndUserId(chatId, userId);
//        if (exists) {
//            return;
//        }
//
//        Chat chat = chatRepository.findById(chatId)
//                .orElseThrow(() -> new EntityNotFoundException("Chat not found: " + chatId));
//
//        addMemberInternal(chat, userId, chatRole);
//    }
//
//
//    public void removeMember(Long chatId, Long userId) {
//        chatMemberRepository.findByChatChatIdAndUserId(chatId, userId)
//                .ifPresent(chatMemberRepository::delete);
//    }
//
//    public void assertUserInChat(Long userId, Long chatId) {
//        boolean exists = chatMemberRepository.existsByChatChatIdAndUserId(chatId, userId);
//        if (!exists) {
//            throw new IllegalStateException("User " + userId + " is not a member of chat " + chatId);
//        }
//
//    }
//
//    public List<ChatDTO> getChatsForUser(Long userId) {
//
//
//        List<Chat> chats = chatRepository.findAllByUserId(userId);
//
//
//        return chats.stream()
//                .map(this::toDtoWithLastMessage)
//                .toList();
//    }
//}

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public ChatServiceImpl(
            ChatRepository chatRepository,
            ChatMemberRepository chatMemberRepository,
            UserRepository userRepository,
            MessageRepository messageRepository,
            ChatMapper chatMapper,
            MessageMapper messageMapper
    ) {
        this.chatRepository = chatRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
    }

    // ---------- helpers ----------

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void addMemberInternal(Chat chat, Long userId, String role) {
        if (chatMemberRepository.existsByChatChatIdAndUserId(chat.getChatId(), userId)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ChatMember member = new ChatMember();
        member.setChat(chat);
        member.setUser(user);
        member.setChatRole(role);

        chatMemberRepository.save(member);
    }

    private ChatDTO toDtoWithLastMessage(Chat chat) {
        ChatDTO dto = chatMapper.toDto(chat);

        MessageDTO lastMessage = messageRepository
                .findFirstByChatChatIdOrderBySentAtDesc(chat.getChatId())
                .map(messageMapper::toDto)
                .orElse(null);

        return new ChatDTO(
                dto.chatId(),
                dto.name(),
                dto.groupChat(),
                dto.members(),
                lastMessage
        );
    }

    // ---------- public API ----------

    @Override
    public List<ChatDTO> getChatsForCurrentUser(String email) {
        User me = getUserByEmail(email);

        return chatRepository.findAllByUserId(me.getId())
                .stream()
                .map(this::toDtoWithLastMessage)
                .toList();
    }

    @Override
    public ChatDTO getChatForCurrentUser(Long chatId, String email) {
        User me = getUserByEmail(email);
        assertUserInChat(me.getId(), chatId);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        return toDtoWithLastMessage(chat);
    }

    @Override
    public ChatDTO createDirectChat(String email, Long otherUserId) {
        User me = getUserByEmail(email);

        if (me.getId().equals(otherUserId)) {
            throw new IllegalArgumentException("Cannot create chat with yourself");
        }

        Chat chat = chatRepository
                .findDirectChatBetweenUsers(me.getId(), otherUserId)
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.setGroupChat(false);

                    Chat saved = chatRepository.save(newChat);
                    addMemberInternal(saved, me.getId(), "Member");
                    addMemberInternal(saved, otherUserId, "Member");

                    return saved;
                });

        return toDtoWithLastMessage(chat);
    }

    @Override
    public ChatDTO createGroupChat(String email, String name, List<Long> memberIds) {
        User me = getUserByEmail(email);

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Group name is required");
        }

        Chat chat = new Chat();
        chat.setGroupChat(true);
        chat.setName(name);

        Chat saved = chatRepository.save(chat);

        addMemberInternal(saved, me.getId(), "Admin");

        if (memberIds != null) {
            memberIds.stream()
                    .filter(id -> !id.equals(me.getId()))
                    .forEach(id -> addMemberInternal(saved, id, "Member"));
        }

        return toDtoWithLastMessage(saved);
    }

    @Override
    public void addMember(Long chatId, String requesterEmail, Long newMemberId, String role) {
        User requester = getUserByEmail(requesterEmail);
        assertUserInChat(requester.getId(), chatId);

        addMemberInternal(
                chatRepository.findById(chatId)
                        .orElseThrow(() -> new EntityNotFoundException("Chat not found")),
                newMemberId,
                role
        );
    }

    @Override
    public void removeMember(Long chatId, String requesterEmail, Long memberId) {
        User requester = getUserByEmail(requesterEmail);
        assertUserInChat(requester.getId(), chatId);

        chatMemberRepository.findByChatChatIdAndUserId(chatId, memberId)
                .ifPresent(chatMemberRepository::delete);
    }

    @Override
    public void assertUserInChat(Long userId, Long chatId) {
        if (!chatMemberRepository.existsByChatChatIdAndUserId(chatId, userId)) {
            throw new IllegalStateException("User is not a member of this chat");
        }
    }
}

