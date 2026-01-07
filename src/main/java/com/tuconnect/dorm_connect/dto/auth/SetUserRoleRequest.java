package com.tuconnect.dorm_connect.dto.auth;

import com.tuconnect.dorm_connect.model.Roles;
import jakarta.validation.constraints.NotNull;

public record SetUserRoleRequest(
        @NotNull Roles role
) {}