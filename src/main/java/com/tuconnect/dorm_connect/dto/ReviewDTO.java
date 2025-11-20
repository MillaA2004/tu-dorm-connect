package com.tuconnect.dorm_connect.dto;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        Integer rating,
        String comment,
        String categoryScoresJson,
        LocalDateTime createdAt,
        Long userId,
        Long dormId
) {}
