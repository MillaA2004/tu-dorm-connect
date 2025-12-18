package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Chat.ChatDTO;
import com.tuconnect.dorm_connect.dto.Chat.ChatMemberDTO;
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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;



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
        member.setLastReadAt(Instant.now());


        chatMemberRepository.save(member);
    }



    private ChatDTO toDtoWithLastMessage(Chat chat, User me) {

        Long adminId = chatMemberRepository
                .findAllByChatChatId(chat.getChatId())
                .stream()
                .filter(m -> "Admin".equalsIgnoreCase(m.getChatRole()))
                .map(m -> m.getUser().getId())
                .findFirst()
                .orElse(null);

        ChatDTO dto = chatMapper.toDto(chat);

        MessageDTO lastMessage = messageRepository
                .findFirstByChatChatIdOrderBySentAtDesc(chat.getChatId())
                .map(messageMapper::toDto)
                .orElse(null);


        Instant lastReadAt = chatMemberRepository
                .findByChatChatIdAndUserId(chat.getChatId(), me.getId())
                .map(ChatMember::getLastReadAt)
                .orElse(Instant.EPOCH);

        if (lastReadAt == null) lastReadAt = Instant.EPOCH;

        long unreadCount = messageRepository
                .countByChatChatIdAndSentAtAfterAndSender_IdNot(
                        chat.getChatId(),
                        lastReadAt,
                        me.getId()
                );

        return new ChatDTO(
                dto.chatId(),
                dto.name(),
                dto.groupChat(),
                adminId,
                dto.members(),
                lastMessage,
                unreadCount
        );
    }


    // ---------- public API ----------

    @Override
    public List<ChatDTO> getChatsForCurrentUser(String email) {
        User me = getUserByEmail(email);

        return chatRepository.findAllByUserId(me.getId())
                .stream()
                .map(chat -> toDtoWithLastMessage(chat, me))
                .toList();
    }

    @Override
    public ChatDTO getChatForCurrentUser(Long chatId, String email) {
        User me = getUserByEmail(email);
        assertUserInChat(me.getId(), chatId);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));


        return toDtoWithLastMessage(chat, me);
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


        return toDtoWithLastMessage(chat, me);
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


        return toDtoWithLastMessage(saved,me);
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

    private boolean isAdmin(Long chatId, Long userId) {
        return chatMemberRepository.findByChatChatIdAndUserId(chatId, userId)
                .map(m -> "Admin".equalsIgnoreCase(m.getChatRole()))
                .orElse(false);
    }

    @Override
    public List<ChatMemberDTO> getChatMembers(Long chatId, String email) {
        User me = getUserByEmail(email);
        assertUserInChat(me.getId(), chatId);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        if (!chat.isGroupChat()) {
            throw new IllegalStateException("Members list is only available for group chats");
        }

        return chatMemberRepository.findAllByChatChatId(chatId)
                .stream()
                .map(cm -> new ChatMemberDTO(
                        cm.getChatMemberId(),
                        cm.getUser().getId(),
                        cm.getUser().getFirstName(),
                        cm.getUser().getLastName(),
                        cm.getChatRole()
                ))
                .toList();
    }


    @Transactional
    public void deleteDirectChatIfEmpty(Long chatId, String email) {
        User me = getUserByEmail(email);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));


        if (Boolean.TRUE.equals(chat.isGroupChat())) return;


        assertUserInChat(me.getId(), chatId);


        boolean hasMessages = messageRepository.existsByChatChatId(chatId);
        if (hasMessages) return;


        chatMemberRepository.deleteByChatChatId(chatId);
        chatRepository.delete(chat);
    }


    public void markAsRead(Long chatId, String email) {
        User me = getUserByEmail(email);

        ChatMember cm = chatMemberRepository
                .findByChatChatIdAndUserId(chatId, me.getId())
                .orElseThrow();

        cm.setLastReadAt(Instant.now());
        chatMemberRepository.save(cm);
    }


}

