package com.tuconnect.dorm_connect.mapper;


import com.tuconnect.dorm_connect.dto.notification.NotificationDTO;
import com.tuconnect.dorm_connect.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    @Mapping(target = "read", expression = "java(notification.getReadAt() != null)")
    @Mapping(target = "actorId", source = "actor.id")
    @Mapping(target = "actorName",
            expression = "java(notification.getActor() != null ? " +
                    "notification.getActor().getFirstName() + \" \" + " +
                    "notification.getActor().getLastName() : null)")
    @Mapping(target = "actorImageUrl", source = "actor.profileImageUrl")
    NotificationDTO toDto(Notification notification);


    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "actor", ignore = true)
    @Mapping(target = "readAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Notification toEntity(NotificationDTO dto);
}

