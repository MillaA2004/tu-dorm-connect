package com.tuconnect.dorm_connect.dto.UserMatch;

import com.tuconnect.dorm_connect.dto.User.UserListingSummaryDTO;

public record UserMatchDTO(
        UserListingSummaryDTO poster,
        Double score
) {
}
