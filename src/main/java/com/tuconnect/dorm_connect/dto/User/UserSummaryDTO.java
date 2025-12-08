package com.tuconnect.dorm_connect.dto.User;

public record UserSummaryDTO(
        Long id,
        String firstName,
        String lastName,
        String major,
        String profileImageUrl,
        String dorm,
        Integer academicYear
) {}
