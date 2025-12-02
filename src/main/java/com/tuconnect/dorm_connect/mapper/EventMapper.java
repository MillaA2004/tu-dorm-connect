
package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Event.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import com.tuconnect.dorm_connect.model.Event;
import com.tuconnect.dorm_connect.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Event toEntity(EventRequestDTO dto);


    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(
            target = "participantIds",
            expression = "java(toParticipantIds(event.getParticipants()))"
    )
    EventResponseDTO toDTO(Event event);


    default Set<Long> toParticipantIds(Set<User> participants) {
        if (participants == null) return Set.of();
        return participants.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }
}

