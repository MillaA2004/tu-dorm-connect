package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Messages.MessageDTO;

import com.tuconnect.dorm_connect.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;


//@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
//public interface MessageMapper {
//
//
//    @Mapping(target = "chatId", source = "chat.chatId")
//    @Mapping(target = "userId", source = "sender.id")
//    MessageDTO toDto(Message message);
//
//
//    @Mapping(target = "chat", ignore = true)
//    @Mapping(target = "sender", ignore = true)
//    Message toEntity(MessageDTO dto);
//}

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {

    @Mapping(target = "chatId", source = "chat.chatId")
    @Mapping(target = "userId", source = "sender.id")
    @Mapping(target = "senderName", expression = "java(message.getSender().getFirstName() + \" \" + message.getSender().getLastName())")
    @Mapping(target = "senderImageUrl", source = "sender.profileImageUrl")
    MessageDTO toDto(Message message);

    @Mapping(target = "chat", ignore = true)
    @Mapping(target = "sender", ignore = true)
    Message toEntity(MessageDTO dto);
}

