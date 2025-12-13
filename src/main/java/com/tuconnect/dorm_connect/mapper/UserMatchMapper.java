package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.User.UserListingSummaryDTO;
import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.model.UserMatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMatchMapper {

    @Mapping(target = "poster", expression = "java(toUserListingSummaryDTO(match.getPoster()))")
    UserMatchDTO toDTO(UserMatch match);

    default UserListingSummaryDTO toUserListingSummaryDTO(User user) {
        if (user == null) return null;

        return new UserListingSummaryDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMajor(),
                user.getProfileImageUrl(),
                user.getDorm() != null ? user.getDorm().getName() : null,
                user.getAcademicYear()
        );
    }
}