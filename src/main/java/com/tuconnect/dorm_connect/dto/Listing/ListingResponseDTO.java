package com.tuconnect.dorm_connect.dto.Listing;

import java.time.LocalDateTime;

public record ListingResponseDTO(
        Long id,
        String title,
        String description,
        Double price,
        String preferencesJson,
        Long dormId,
        String dormName,
        Long userId,
        String username,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}
