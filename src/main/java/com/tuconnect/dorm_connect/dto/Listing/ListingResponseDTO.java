package com.tuconnect.dorm_connect.dto.Listing;

import com.tuconnect.dorm_connect.dto.Dorm.DormSummaryDTO;
import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;

import java.time.LocalDateTime;

public record ListingResponseDTO(
        Long id,
        String title,
        String description,
        Double price,
        String preferencesJson,
        DormSummaryDTO dorm,
        UserSummaryDTO poster,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}
