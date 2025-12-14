package com.tuconnect.dorm_connect.dto.Chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

//public record CreateGroupChatRequest(
//        @NotNull Long currentUserId,
//        @NotBlank String name,
//        List<Long> memberIds
//) {
//}

public record CreateGroupChatRequest(
        @NotBlank String name,
        List<Long> memberIds
) {}