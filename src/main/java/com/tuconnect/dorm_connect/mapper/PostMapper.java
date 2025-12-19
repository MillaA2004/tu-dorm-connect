package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Post.PostResponse;
import com.tuconnect.dorm_connect.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "comments", source = "comments")
    PostResponse toDTO(Post post);

}
