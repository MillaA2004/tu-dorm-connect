package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Comment.CommentResponse;
import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;
import com.tuconnect.dorm_connect.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "author", source = "author")
    CommentResponse toDTO(Comment comment);

    default UserSummaryDTO toUserSummaryDTO(com.tuconnect.dorm_connect.model.User user) {
        if (user == null) return null;
        return new UserSummaryDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl()
        );
    }
}

