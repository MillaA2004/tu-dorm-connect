package com.tuconnect.dorm_connect.dto.auth;

import jakarta.validation.constraints.Min;

public record SuspendUserRequest(
        @Min(1) long minutes
) {}