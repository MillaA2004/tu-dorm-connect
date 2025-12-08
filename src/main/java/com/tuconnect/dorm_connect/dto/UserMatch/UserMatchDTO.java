package com.tuconnect.dorm_connect.dto.UserMatch;

import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;

public record UserMatchDTO(
        UserSummaryDTO poster,
        Double score
) {
}
