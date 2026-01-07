package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.User.UserDTO;
import com.tuconnect.dorm_connect.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", source = "dto.userId")
    @Mapping(target = "firstName", source = "dto.firstName")
    @Mapping(target = "lastName", source = "dto.lastName")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "password", source = "dto.password")
    @Mapping(target = "profileImageUrl", source = "dto.profileImageUrl")
    @Mapping(target = "gender", source = "dto.gender")
    @Mapping(target = "major", source = "dto.major")
    @Mapping(target = "academicYear", source = "dto.year")
    @Mapping(target = "role", source = "dto.role")
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "questionnaire", ignore = true)
    @Mapping(target = "listings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "organizedEvents", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "year", source = "academicYear")
    UserDTO toDTO(User user);
}
