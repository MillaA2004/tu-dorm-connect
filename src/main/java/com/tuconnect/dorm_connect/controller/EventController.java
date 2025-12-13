package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Event.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import com.tuconnect.dorm_connect.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/creator/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsCreatedByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getAllEventsCreatedByUser(userId));
    }


    @GetMapping("/participant/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsAUserParticipatesIn(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getAllEventsAUserParticipatesIn(userId));
    }


    @PostMapping("/creator/{creatorId}")
    public ResponseEntity<EventResponseDTO> createEvent(
            @PathVariable Long creatorId,
            @RequestBody EventRequestDTO dto
    ) {
        EventResponseDTO created = eventService.createEvent(creatorId, dto);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/{eventId}/creator/{creatorId}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long creatorId,
            @PathVariable Long eventId,
            @RequestBody EventRequestDTO dto
    ) {
        EventResponseDTO updated = eventService.updateEvent(creatorId, eventId, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{eventId}/creator/{userId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ) {
        eventService.deleteEvent(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/join/{userId}")
    public ResponseEntity<EventResponseDTO> joinEvent(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ) {
        EventResponseDTO dto = eventService.joinEvent(eventId, userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{eventId}/leave/{userId}")
    public ResponseEntity<EventResponseDTO> leaveEvent(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ) {
        EventResponseDTO dto = eventService.leaveEvent(eventId, userId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{eventId}/participants/{participantId}/creator/{creatorId}")
    public ResponseEntity<EventResponseDTO> removeParticipant(
            @PathVariable Long eventId,
            @PathVariable Long participantId,
            @PathVariable Long creatorId
    ) {
        return ResponseEntity.ok(eventService.removeParticipant(eventId, participantId, creatorId));
    }


    @GetMapping("/search")
    public List<EventResponseDTO> search(@RequestParam String q) {
        return eventService.searchEvents(q);
    }


}
