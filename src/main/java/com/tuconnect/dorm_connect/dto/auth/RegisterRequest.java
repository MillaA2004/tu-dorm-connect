package com.tuconnect.dorm_connect.dto.auth;

import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String profileImageUrl,
        User.Gender gender,
        String major,
        Integer year,
        Roles role

) {}
