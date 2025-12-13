package com.tuconnect.dorm_connect.dto.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        Long authorId,
        String authorFirstName,
        String authorLastName
) {}
