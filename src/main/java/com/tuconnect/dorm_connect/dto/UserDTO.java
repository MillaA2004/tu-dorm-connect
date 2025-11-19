package com.tuconnect.dorm_connect.dto;

import com.tuconnect.dorm_connect.model.Roles;

public record UserDTO(


        Long userId,
        String firstName,
        String lastName,
        String email,
        String password,
        String profileImageUrl,
        String major,
        Integer year,
        Roles role

)
{
}
