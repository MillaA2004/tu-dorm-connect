package com.tuconnect.dorm_connect.dto.Post;

public record PostCreateRequest(
        Long authorId,
        String content
) {}