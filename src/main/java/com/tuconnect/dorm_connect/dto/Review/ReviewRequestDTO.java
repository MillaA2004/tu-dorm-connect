package com.tuconnect.dorm_connect.dto.Review;

public record ReviewRequestDTO(
        Integer rating,
        String comment,
        Long dormId
) {}