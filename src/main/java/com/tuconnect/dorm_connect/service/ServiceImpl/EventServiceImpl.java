package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.EventResponseDTO;
import com.tuconnect.dorm_connect.mapper.EventMapper;
import com.tuconnect.dorm_connect.model.Event;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.EventRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    @Autowired
    public EventServiceImpl(EventMapper eventMapper,EventRepository eventRepository,UserRepository userRepository) {
        this.eventMapper = eventMapper;
        this.eventRepository= eventRepository;
        this.userRepository=userRepository;
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

        if(!currentEvent.getCreator().equals(creator) || !creator.getRole().equals(Roles.Admin)) {
            throw new IllegalArgumentException("creator is not the owner of the event!");
        }

        eventRepository.delete(currentEvent);


    }

    public EventResponseDTO getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        return eventMapper.toDTO(event);
    }

    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll()
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

        Event saved = eventRepository.save(event);
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
}
