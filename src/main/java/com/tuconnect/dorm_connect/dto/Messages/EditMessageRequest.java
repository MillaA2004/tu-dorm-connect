package com.tuconnect.dorm_connect.dto.Messages;

import jakarta.validation.constraints.NotBlank;

public record EditMessageRequest(
        @NotBlank String content
) {}
