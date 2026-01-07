package com.tuconnect.dorm_connect.service;


import com.tuconnect.dorm_connect.dto.Event.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import com.tuconnect.dorm_connect.mapper.EventMapper;
import com.tuconnect.dorm_connect.model.*;
import com.tuconnect.dorm_connect.repository.ChatMemberRepository;
import com.tuconnect.dorm_connect.repository.ChatRepository;
import com.tuconnect.dorm_connect.repository.EventRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.NotificationService;
import com.tuconnect.dorm_connect.service.implementations.EventServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock private EventMapper eventMapper;
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private ChatRepository chatRepository;
    @Mock private ChatMemberRepository chatMemberRepository;

    @Mock private Authentication authentication;

    @InjectMocks
    private EventServiceImpl service;

    private User creator;
    private User user;
    private Event event;
    private Chat chat;

    @BeforeEach
    void setUp() {
        creator = User.builder()
                .id(1L)
                .email("creator@test.com")
                .role(Roles.User)
                .build();
        creator.setEvents(new ArrayList<>());

        user = User.builder()
                .id(2L)
                .email("user@test.com")
                .role(Roles.User)
                .build();
        user.setEvents(new ArrayList<>());

        chat = new Chat();
        chat.setChatId(100L);
        chat.setGroupChat(true);
        chat.setName("Chat");

        event = new Event();
        event.setEventId(10L);
        event.setTitle("Title");
        event.setDescription("Desc");
        event.setAddress("Addr");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setCapacity(null);
        event.setCreator(creator);
        event.setParticipants(new HashSet<>());
        event.setChat(chat);
    }

    // -------------------- createEvent --------------------

    @Test
    void createEvent_success_createsEventChatAndAdminMember() {
        when(authentication.getName()).thenReturn("creator@test.com");
        when(userRepository.findByEmail("creator@test.com")).thenReturn(Optional.of(creator));

        EventRequestDTO req = new EventRequestDTO(
                "My Event", "My Desc", "My Addr",
                LocalDateTime.now().plusDays(2),
                20, "Party", 42.0, 23.0
        );

        Event mapped = new Event();
        mapped.setTitle(req.title());
        mapped.setParticipants(new HashSet<>());

        when(eventMapper.toEntity(req)).thenReturn(mapped);

        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            if (e.getEventId() == null) e.setEventId(55L);
            return e;
        });

        when(chatRepository.save(any(Chat.class))).thenAnswer(inv -> {
            Chat c = inv.getArgument(0);
            c.setChatId(999L);
            return c;
        });

        EventResponseDTO resp = mock(EventResponseDTO.class);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(resp);

        EventResponseDTO result = service.createEvent(authentication, req);

        assertSame(resp, result);

        verify(chatRepository).save(argThat(c -> c.isGroupChat() && "My Event".equals(c.getName())));
        verify(chatMemberRepository).save(argThat(cm ->
                "Admin".equals(cm.getChatRole()) &&
                        cm.getUser().equals(creator) &&
                        cm.getChat() != null &&
                        cm.getLastReadAt() != null
        ));


        verify(eventRepository, times(2)).save(any(Event.class));
        verify(eventMapper).toDTO(any(Event.class));
    }

    @Test
    void createEvent_userNotFound_throwsEntityNotFound() {
        when(authentication.getName()).thenReturn("missing@test.com");
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.createEvent(authentication, new EventRequestDTO(
                        "t","d","a", LocalDateTime.now().plusDays(1),
                        null, "type", null, null
                )));

        verifyNoInteractions(eventRepository, chatRepository, chatMemberRepository, notificationService);
    }

    // -------------------- updateEvent --------------------

    @Test
    void updateEvent_success_updatesFields() {
        when(authentication.getName()).thenReturn("creator@test.com");
        when(userRepository.findByEmail("creator@test.com")).thenReturn(Optional.of(creator));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        EventRequestDTO req = new EventRequestDTO(
                "New Title", "New Desc", "New Addr",
                LocalDateTime.now().plusDays(3),
                50, "Party", 42.0, 23.0
        );

        EventResponseDTO dto = mock(EventResponseDTO.class);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(dto);

        EventResponseDTO res = service.updateEvent(authentication, 10L, req);

        assertSame(dto, res);
        assertEquals("New Title", event.getTitle());
        assertEquals("New Desc", event.getDescription());
        assertEquals("New Addr", event.getAddress());
        assertEquals(50, event.getCapacity());
        assertEquals("Party", event.getEventType());
        assertEquals(42.0, event.getLatitude());
        assertEquals(23.0, event.getLongitude());

        verify(eventRepository).save(event);
    }

    @Test
    void updateEvent_notOwner_throwsIllegalArgumentException() {
        User other = User.builder().id(999L).email("other@test.com").role(Roles.User).build();

        when(authentication.getName()).thenReturn("other@test.com");
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateEvent(authentication, 10L, new EventRequestDTO(
                        "t","d","a", LocalDateTime.now().plusDays(1),
                        null, "type", null, null
                )));

        verify(eventRepository, never()).save(any());
    }

    // -------------------- deleteEvent --------------------

    @Test
    void deleteEvent_owner_success() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("creator@test.com");
        when(userRepository.findByEmail("creator@test.com")).thenReturn(Optional.of(creator));

        service.deleteEvent(10L, authentication);

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteEvent_admin_success_evenIfNotOwner() {
        User admin = User.builder().id(500L).email("admin@test.com").role(Roles.Admin).build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("admin@test.com");
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        service.deleteEvent(10L, authentication);

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteEvent_notAllowed_throwsIllegalArgumentException() {
        User other = User.builder().id(999L).email("other@test.com").role(Roles.User).build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("other@test.com");
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteEvent(10L, authentication));

        verify(eventRepository, never()).delete(any());
    }

    // -------------------- joinEvent --------------------

    @Test
    void joinEvent_success_addsParticipant_addsChatMember_notifiesCreator() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        when(chatMemberRepository.existsByChatChatIdAndUserId(chat.getChatId(), user.getId()))
                .thenReturn(false);

        EventResponseDTO dto = mock(EventResponseDTO.class);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(dto);

        EventResponseDTO res = service.joinEvent(10L, authentication);

        assertSame(dto, res);
        assertTrue(event.getParticipants().contains(user));
        assertTrue(user.getEvents().contains(event));

        verify(chatMemberRepository).save(argThat(cm ->
                "Member".equals(cm.getChatRole()) &&
                        cm.getUser().equals(user) &&
                        cm.getChat().equals(chat) &&
                        cm.getLastReadAt() != null
        ));

        verify(notificationService).notifyEventJoined(creator.getId(), user.getId(), event.getEventId());
    }

    @Test
    void joinEvent_alreadyJoined_throwsIllegalArgumentException() {
        event.getParticipants().add(user);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> service.joinEvent(10L, authentication));

        verify(eventRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    void joinEvent_full_throwsIllegalStateException() {
        event.setCapacity(1);
        event.getParticipants().add(new User());

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> service.joinEvent(10L, authentication));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void joinEvent_inPast_throwsIllegalArgumentException() {
        event.setDateTime(LocalDateTime.now().minusHours(1));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> service.joinEvent(10L, authentication));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void joinEvent_chatAlreadyMember_doesNotCreateChatMember() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        when(chatMemberRepository.existsByChatChatIdAndUserId(chat.getChatId(), user.getId()))
                .thenReturn(true);

        when(eventMapper.toDTO(any(Event.class))).thenReturn(mock(EventResponseDTO.class));

        service.joinEvent(10L, authentication);

        verify(chatMemberRepository, never()).save(argThat(cm -> "Member".equals(cm.getChatRole())));
    }

    // -------------------- leaveEvent --------------------

    @Test
    void leaveEvent_success_removesParticipantAndUserEventLink() {
        event.getParticipants().add(user);
        user.getEvents().add(event);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        EventResponseDTO dto = mock(EventResponseDTO.class);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(dto);

        EventResponseDTO res = service.leaveEvent(10L, authentication);

        assertSame(dto, res);
        assertFalse(event.getParticipants().contains(user));
        assertFalse(user.getEvents().contains(event));
        verify(eventRepository).save(event);
    }

    @Test
    void leaveEvent_notParticipant_throwsIllegalArgumentException() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> service.leaveEvent(10L, authentication));
        verify(eventRepository, never()).save(any());
    }

    // -------------------- removeParticipant --------------------

    @Test
    void removeParticipant_success_creatorRemovesUser() {
        event.getParticipants().add(user);
        user.getEvents().add(event);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("creator@test.com");
        when(userRepository.findByEmail("creator@test.com")).thenReturn(Optional.of(creator));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        EventResponseDTO dto = mock(EventResponseDTO.class);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(dto);

        EventResponseDTO res = service.removeParticipant(10L, user.getId(), authentication);

        assertSame(dto, res);
        assertFalse(event.getParticipants().contains(user));
        assertFalse(user.getEvents().contains(event));
        verify(eventRepository).save(event);
    }

    @Test
    void removeParticipant_notCreator_throwsAccessDenied() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class,
                () -> service.removeParticipant(10L, user.getId(), authentication));
    }

    @Test
    void removeParticipant_cannotRemoveCreator_throwsIllegalArgumentException() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("creator@test.com");
        when(userRepository.findByEmail("creator@test.com")).thenReturn(Optional.of(creator));

        assertThrows(IllegalArgumentException.class,
                () -> service.removeParticipant(10L, creator.getId(), authentication));
    }

    @Test
    void removeParticipant_targetNotParticipant_throwsIllegalArgumentException() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(authentication.getName()).thenReturn("creator@test.com");
        when(userRepository.findByEmail("creator@test.com")).thenReturn(Optional.of(creator));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));


        assertThrows(IllegalArgumentException.class,
                () -> service.removeParticipant(10L, user.getId(), authentication));

        verify(eventRepository, never()).save(any());
    }
}
