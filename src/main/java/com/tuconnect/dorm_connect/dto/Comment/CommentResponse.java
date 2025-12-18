package com.tuconnect.dorm_connect.dto.Comment;

import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        UserSummaryDTO author
) {}