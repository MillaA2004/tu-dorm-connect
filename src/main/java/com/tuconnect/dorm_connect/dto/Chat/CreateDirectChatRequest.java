package com.tuconnect.dorm_connect.dto.Chat;

import jakarta.validation.constraints.NotNull;

//public record CreateDirectChatRequest(
//        @NotNull Long currentUserId,
//        @NotNull Long otherUserId
//) {
//}

public record CreateDirectChatRequest(
        @NotNull Long otherUserId
) {}
