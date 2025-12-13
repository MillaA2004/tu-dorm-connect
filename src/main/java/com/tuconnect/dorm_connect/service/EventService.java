package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Event.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    List<EventResponseDTO> getAllEvents();

    EventResponseDTO getEventById(Long eventId);

    List<EventResponseDTO> getAllEventsCreatedByUser(Long userId);

    List<EventResponseDTO> getAllEventsAUserParticipatesIn(Long userId);

    EventResponseDTO createEvent(Long creatorId, EventRequestDTO dto);

    EventResponseDTO updateEvent(Long creatorId, Long eventId, EventRequestDTO dto);

    void deleteEvent(Long eventId, Long userId);

    EventResponseDTO joinEvent(Long eventId, Long userId);

    EventResponseDTO leaveEvent(Long eventId, Long userId);

    EventResponseDTO removeParticipant(Long eventId, Long participantId, Long requesterId);

    List<EventResponseDTO> searchEvents(String q);
}
