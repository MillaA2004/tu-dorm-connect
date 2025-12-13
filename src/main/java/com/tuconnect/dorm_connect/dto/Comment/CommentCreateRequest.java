package com.tuconnect.dorm_connect.dto.Comment;

public record CommentCreateRequest(
        Long postId,
        Long authorId,
        String content
) {}