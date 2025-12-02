package com.tuconnect.dorm_connect.dto.Review;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long id,
        Integer rating,
        String comment,
        String categoryScoresJson,
        LocalDateTime createdAt,
        Long userId,
        Long dormId
) {}
