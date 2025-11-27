package com.tuconnect.dorm_connect.dto.Chat;

import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(
        @NotNull Long userId,
        @NotNull String chatRole
) {
}
