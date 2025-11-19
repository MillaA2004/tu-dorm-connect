//package com.tuconnect.dorm_connect.mapper;
//
//import com.tuconnect.dorm_connect.dto.EventRequestDTO;
//import com.tuconnect.dorm_connect.dto.EventResponseDTO;
//import com.tuconnect.dorm_connect.model.Event;
//import com.tuconnect.dorm_connect.model.EventsTypes;
//import com.tuconnect.dorm_connect.model.User;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingConstants;
//
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
//public interface EventMapper {
//
//    // -------- Request DTO -> Entity (for create/update) --------
//    @Mapping(target = "eventId", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "creator", ignore = true)       // set in service
//    @Mapping(target = "participants", ignore = true)  // handled in service
//    @Mapping(target = "eventType",
//            expression = "java(dto.eventType() != null ? com.tuconnect.dorm_connect.model.EventsTypes.valueOf(dto.eventType()) : null)")
//    Event toEntity(EventRequestDTO dto);
//
//    // -------- Entity -> Response DTO --------
//    @Mapping(target = "eventId", source = "eventId")
//    @Mapping(target = "title", source = "title")
//    @Mapping(target = "description", source = "description")
//    @Mapping(target = "address", source = "address")
//    @Mapping(target = "dateTime", source = "dateTime")
//    @Mapping(target = "capacity", source = "capacity")
//    @Mapping(target = "createdAt", source = "createdAt")
//    @Mapping(target = "eventType", source = "eventType")   // uses helper map(EventsTypes)
//    @Mapping(target = "latitude", source = "latitude")
//    @Mapping(target = "longitude", source = "longitude")
//    @Mapping(target = "creatorId", source = "creator.userId")
//    @Mapping(
//            target = "participantIds",
//            expression = "java(toParticipantIds(event.getParticipants()))"
//    )
//    EventResponseDTO toDTO(Event event);
//
//    // -------- helper methods MapStruct will use --------
//
//
//
//    // Map participants Set<User> -> Set<Long>
//    default Set<Long> toParticipantIds(Set<User> participants) {
//        if (participants == null) {
//            return Set.of();
//        }
//        return participants.stream()
//                .map(User::getUserId)   // make sure your User has getUserId()
//                .collect(Collectors.toSet());
//    }
//}
package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.EventRequestDTO;
import com.tuconnect.dorm_connect.dto.EventResponseDTO;
import com.tuconnect.dorm_connect.model.Event;
import com.tuconnect.dorm_connect.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    // DTO -> Entity
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "participants", ignore = true)
    // eventType is String in both DTO and Entity, so no special mapping needed
    Event toEntity(EventRequestDTO dto);

    // Entity -> DTO
    @Mapping(target = "creatorId", source = "creator.userId")
    @Mapping(
            target = "participantIds",
            expression = "java(toParticipantIds(event.getParticipants()))"
    )
    EventResponseDTO toDTO(Event event);

    // helper method for participants
    default Set<Long> toParticipantIds(Set<User> participants) {
        if (participants == null) return Set.of();
        return participants.stream()
                .map(User::getUserId)   // adjust if your getter is different
                .collect(Collectors.toSet());
    }
}

