package com.tuconnect.dorm_connect.dto.User;

import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User.Sex;
import com.tuconnect.dorm_connect.model.User.SearchingStatus;


public record UserDTO(


        Long userId,
        String firstName,
        String lastName,
        String email,
        String password,
        String profileImageUrl,
        Sex sex,
        SearchingStatus searchingStatus,
        String major,
        Integer year,
        Roles role
)
{
}
