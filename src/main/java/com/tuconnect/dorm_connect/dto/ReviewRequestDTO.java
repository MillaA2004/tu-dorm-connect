package com.tuconnect.dorm_connect.dto;

public record ReviewRequestDTO(
        Integer rating,
        String comment,
        String categoryScoresJson,
        Long userId,
        Long dormId
) {}