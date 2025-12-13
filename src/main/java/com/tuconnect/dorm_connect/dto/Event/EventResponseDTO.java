package com.tuconnect.dorm_connect.dto.Event;

import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;
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
        UserSummaryDTO creator,
        List<UserSummaryDTO> participants
) {

}

