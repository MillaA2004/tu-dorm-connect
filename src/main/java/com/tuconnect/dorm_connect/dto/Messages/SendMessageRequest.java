package com.tuconnect.dorm_connect.dto.Messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//public record SendMessageRequest(
//        @NotNull Long userId,
//        @NotBlank String content
//) {
//}

public record SendMessageRequest(
        @NotBlank String content
) {}
