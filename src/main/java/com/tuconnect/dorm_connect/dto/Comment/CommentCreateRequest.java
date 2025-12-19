package com.tuconnect.dorm_connect.dto.Comment;

public record CommentCreateRequest(
        Long postId,
        String content
) {}