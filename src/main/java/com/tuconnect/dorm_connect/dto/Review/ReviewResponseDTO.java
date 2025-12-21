package com.tuconnect.dorm_connect.dto.Review;

import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long id,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        UserSummaryDTO author,
        Long dormId
) {}
