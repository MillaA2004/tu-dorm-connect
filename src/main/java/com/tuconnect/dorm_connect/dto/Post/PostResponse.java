package com.tuconnect.dorm_connect.dto.Post;

import com.tuconnect.dorm_connect.dto.Comment.CommentResponse;
import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        UserSummaryDTO author,
        List<CommentResponse> comments
) {}
