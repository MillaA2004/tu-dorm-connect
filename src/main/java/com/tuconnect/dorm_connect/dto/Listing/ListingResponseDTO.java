package com.tuconnect.dorm_connect.dto.Listing;

import java.time.LocalDateTime;

public record ListingResponseDTO(
        Long id,
        String title,
        String description,
        Double price,
        String preferencesJson,
        String dormName,
        Long userId,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}
