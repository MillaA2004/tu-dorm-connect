package com.tuconnect.dorm_connect.service.ServiceImpl;

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

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository, ChatMemberRepository chatMemberRepository, UserRepository userRepository, MessageRepository messageRepository, ChatMapper chatMapper, MessageMapper messageMapper) {
        this.chatMapper = chatMapper;
        this.chatRepository = chatRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
        this.messageRepository = messageRepository;
    }

    private void addMemberInternal(Chat chat, Long userId, String chatRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        ChatMember member = new ChatMember();
        member.setChat(chat);
        member.setUser(user);
        member.setChatRole(chatRole);

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


    public ChatDTO getChatById(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found: " + chatId));

        return toDtoWithLastMessage(chat);
    }


    public ChatDTO createDirectChat(Long currentUserId, Long otherUserId) {
        if (currentUserId.equals(otherUserId)) {
            throw new IllegalArgumentException("Cannot create direct chat with yourself");
        }


        Chat chat = chatRepository
                .findDirectChatBetweenUsers(currentUserId, otherUserId)
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.setGroupChat(false);
                    newChat.setName(null);

                    Chat savedChat = chatRepository.save(newChat);

                    addMemberInternal(savedChat, currentUserId, "Member");
                    addMemberInternal(savedChat, otherUserId, "Member");

                    return savedChat;
                });

        return toDtoWithLastMessage(chat);
    }


    public ChatDTO createGroupChat(Long currentUserId, String name, List<Long> memberIds) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Group chat name is required");
        }

        Chat chat = new Chat();
        chat.setGroupChat(true);
        chat.setName(name);

        Chat savedChat = chatRepository.save(chat);


        addMemberInternal(savedChat, currentUserId, "Admin");


        if (memberIds != null) {
            for (Long memberId : memberIds) {
                if (!memberId.equals(currentUserId)) {
                    addMemberInternal(savedChat, memberId, "Member");
                }
            }
        }

        return toDtoWithLastMessage(savedChat);
    }


    public void addMember(Long chatId, Long userId, String chatRole) {
        boolean exists = chatMemberRepository.existsByChatChatIdAndUserUserId(chatId, userId);
        if (exists) {
            return;
        }

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found: " + chatId));

        addMemberInternal(chat, userId, chatRole);
    }


    public void removeMember(Long chatId, Long userId) {
        chatMemberRepository.findByChatChatIdAndUserUserId(chatId, userId)
                .ifPresent(chatMemberRepository::delete);
    }

    public void assertUserInChat(Long userId, Long chatId) {
        boolean exists = chatMemberRepository.existsByChatChatIdAndUserUserId(chatId, userId);
        if (!exists) {
            throw new IllegalStateException("User " + userId + " is not a member of chat " + chatId);
        }

    }

    public List<ChatDTO> getChatsForUser(Long userId) {


        List<Chat> chats = chatRepository.findAllByUserId(userId);


        return chats.stream()
                .map(this::toDtoWithLastMessage)
                .toList();
    }
}
