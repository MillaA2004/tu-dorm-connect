package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Event.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;
import com.tuconnect.dorm_connect.model.Event;
import com.tuconnect.dorm_connect.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Event toEntity(EventRequestDTO dto);


    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "creator", expression = "java(toUserSummary(event.getCreator()))")
    @Mapping(target = "participants", expression = "java(toUserSummaries(event.getParticipants()))")
    EventResponseDTO toDTO(Event event);

    default UserSummaryDTO toUserSummary(User user) {
        if (user == null) return null;
        return new UserSummaryDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl()
        );
    }

    default List<UserSummaryDTO> toUserSummaries(Set<User> users) {
        if (users == null) return List.of();
        return users.stream()
                .map(this::toUserSummary)
                .collect(Collectors.toList());
    }
}
