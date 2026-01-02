package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Event.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import com.tuconnect.dorm_connect.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }


    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }


    @GetMapping("/me/created")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsByCreator(Authentication authentication) {
        return ResponseEntity.ok(eventService.getAllEventsByCreator(authentication));
    }


    @GetMapping("/me/participating")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsAUserParticipatesIn(Authentication authentication) {
        return ResponseEntity.ok(eventService.getAllEventsAUserParticipatesIn(authentication));
    }


    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(
            Authentication authentication,
            @RequestBody EventRequestDTO dto
    ) {
        EventResponseDTO created = eventService.createEvent(authentication, dto);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            Authentication authentication,
            @PathVariable Long eventId,
            @RequestBody EventRequestDTO dto
    ) {
        EventResponseDTO updated = eventService.updateEvent(authentication, eventId, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        eventService.deleteEvent(eventId, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/join")
    public ResponseEntity<EventResponseDTO> joinEvent(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        EventResponseDTO dto = eventService.joinEvent(eventId, authentication);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{eventId}/leave")
    public ResponseEntity<EventResponseDTO> leaveEvent(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        EventResponseDTO dto = eventService.leaveEvent(eventId, authentication);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{eventId}/participants/{participantId}")
    public ResponseEntity<EventResponseDTO> removeParticipant(
            @PathVariable Long eventId,
            @PathVariable Long participantId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(eventService.removeParticipant(eventId, participantId, authentication));
    }


    @GetMapping("/search")
    public List<EventResponseDTO> search(@RequestParam String q) {
        return eventService.searchEvents(q);
    }

    @GetMapping("/creator/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsCreatedByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getAllEventsCreatedByUser(userId));
    }


}
