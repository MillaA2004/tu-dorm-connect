package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Comment.CommentResponse;
import com.tuconnect.dorm_connect.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    CommentResponse toDTO(Comment comment);
}
