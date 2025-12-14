package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Event.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import com.tuconnect.dorm_connect.mapper.EventMapper;
import com.tuconnect.dorm_connect.model.Event;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.EventRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.EventService;
import com.tuconnect.dorm_connect.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    @Autowired
    public EventServiceImpl(EventMapper eventMapper,EventRepository eventRepository,UserRepository userRepository,NotificationService notificationService) {
        this.eventMapper = eventMapper;
        this.eventRepository= eventRepository;
        this.userRepository=userRepository;
        this.notificationService = notificationService;
    }


    public EventResponseDTO createEvent(Long creatorId, EventRequestDTO dto) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + creatorId));

        Event event = eventMapper.toEntity(dto);
        event.setCreator(creator);
        event.setCreatedAt(LocalDateTime.now());



        Event saved = eventRepository.save(event);
        return eventMapper.toDTO(saved);
    }

    @Transactional
    public EventResponseDTO updateEvent(Long creatorId, Long eventId, EventRequestDTO dto) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + creatorId));

        Event currentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        if(!currentEvent.getCreator().equals(creator)) {
            throw new IllegalArgumentException("creator is not the owner of the event!");
        }

        currentEvent.setTitle(dto.title());
        currentEvent.setDescription(dto.description());
        currentEvent.setAddress(dto.address());
        currentEvent.setDateTime(dto.dateTime());
        currentEvent.setCapacity(dto.capacity());
        currentEvent.setEventType(dto.eventType());
        currentEvent.setLatitude(dto.latitude());
        currentEvent.setLongitude(dto.longitude());

        Event updated = eventRepository.save(currentEvent);
        return eventMapper.toDTO(updated);

    }

    public void deleteEvent(Long eventId, Long userId) {

        Event currentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (
                !currentEvent.getCreator().equals(creator)
                        && !creator.getRole().equals(Roles.Admin)
        ) {
            throw new IllegalArgumentException("Not allowed to delete this event.");
        }

        eventRepository.delete(currentEvent);


    }

    public EventResponseDTO getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        return eventMapper.toDTO(event);
    }



    public List<EventResponseDTO> getAllEvents() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        return eventRepository
                .findAllByDateTimeAfterOrderByDateTimeAsc(cutoff)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }




    @Transactional
    public List<EventResponseDTO> getAllEventsCreatedByUser(Long userId) {
        return eventRepository.findAllByCreator_Id(userId)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }


    @Transactional
    public List<EventResponseDTO> getAllEventsAUserParticipatesIn(Long userId) {
        return eventRepository.findAllByParticipants_Id(userId)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    @Transactional
    public EventResponseDTO joinEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (event.getParticipants().contains(user)) {
            throw new IllegalArgumentException("User already joined this event.");
        }

        if (event.getCapacity() != null && event.getParticipants().size() >= event.getCapacity()) {
            throw new IllegalStateException("Event is full.");
        }

        event.getParticipants().add(user);
        if (user.getEvents() != null && !user.getEvents().contains(event)) {
            user.getEvents().add(event);
        }

        LocalDateTime currTime = LocalDateTime.now();

        if(currTime.isAfter(event.getDateTime())) {
            throw new IllegalArgumentException("Event already happened");
        }

        Event saved = eventRepository.save(event);

        if (saved.getCreator() != null) {
            notificationService.notifyEventJoined(
                    saved.getCreator().getId(),
                    user.getId(),
                    saved.getEventId()
            );
        }

        return eventMapper.toDTO(saved);
    }

    @Transactional
    public EventResponseDTO leaveEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (!event.getParticipants().contains(user)) {
            throw new IllegalArgumentException("User is not a participant of this event.");
        }

        event.getParticipants().remove(user);
        if (user.getEvents() != null) {
            user.getEvents().remove(event);
        }

        Event saved = eventRepository.save(event);
        return eventMapper.toDTO(saved);
    }

    @Transactional
    public EventResponseDTO removeParticipant(Long eventId, Long participantId, Long requesterId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));


        if (event.getCreator() == null || !event.getCreator().getId().equals(requesterId)) {
            throw new AccessDeniedException("Only the event creator can remove participants.");
        }


        if (event.getCreator().getId().equals(participantId)) {
            throw new IllegalArgumentException("Creator cannot be removed from the event.");
        }

        User participant = userRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + participantId));

        if (!event.getParticipants().contains(participant)) {
            throw new IllegalArgumentException("User is not a participant of this event.");
        }

        event.getParticipants().remove(participant);

        if (participant.getEvents() != null) {
            participant.getEvents().remove(event);
        }

        Event saved = eventRepository.save(event);
        return eventMapper.toDTO(saved);
    }


    @Transactional
    public List<EventResponseDTO> searchEvents(String q) {

        String query = q.trim();

        return eventRepository.searchByTitleOrAddress(query)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }



}
