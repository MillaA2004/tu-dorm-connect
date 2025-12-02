package com.tuconnect.dorm_connect.dto.Event;

import java.time.LocalDateTime;

public record EventRequestDTO(
        String title,
        String description,
        String address,
        LocalDateTime dateTime,
        Integer capacity,
        String eventType,
        Double latitude,
        Double longitude
) {}
