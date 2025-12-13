package com.tuconnect.dorm_connect.dto.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        Long authorId,
        String authorFirstName,
        String authorLastName
) {}