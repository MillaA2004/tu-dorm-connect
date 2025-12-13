package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Post.PostResponse;
import com.tuconnect.dorm_connect.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    PostResponse toDTO(Post post);
}
