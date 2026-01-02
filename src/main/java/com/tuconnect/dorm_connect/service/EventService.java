package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Event.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    List<EventResponseDTO> getAllEvents();

    EventResponseDTO getEventById(Long eventId);

    List<EventResponseDTO> getAllEventsByCreator(Authentication authentication);

    List<EventResponseDTO> getAllEventsCreatedByUser(Long userId);

    List<EventResponseDTO> getAllEventsAUserParticipatesIn(Authentication authentication);

    EventResponseDTO createEvent(Authentication authentication, EventRequestDTO dto);

    EventResponseDTO updateEvent(Authentication authentication, Long eventId, EventRequestDTO dto);

    void deleteEvent(Long eventId, Authentication authentication);

    EventResponseDTO joinEvent(Long eventId, Authentication authentication);

    EventResponseDTO leaveEvent(Long eventId, Authentication authentication);

    EventResponseDTO removeParticipant(Long eventId, Long participantId, Authentication authentication);

    List<EventResponseDTO> searchEvents(String q);
}
