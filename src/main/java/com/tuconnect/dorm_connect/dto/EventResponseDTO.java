package com.tuconnect.dorm_connect.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record EventResponseDTO(
        Long eventId,
        String title,
        String description,
        String address,
        LocalDateTime dateTime,
        Integer capacity,
        LocalDateTime createdAt,
        String eventType,
        Double latitude,
        Double longitude,
        Long creatorId,
        Set<Long> participantIds
) {}
