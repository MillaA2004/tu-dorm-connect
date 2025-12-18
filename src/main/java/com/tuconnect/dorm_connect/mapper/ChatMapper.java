package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Chat.ChatDTO;
import com.tuconnect.dorm_connect.model.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { ChatMemberMapper.class, MessageMapper.class }
)
public interface ChatMapper {

    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "groupChat", source = "groupChat")
    @Mapping(target = "members", source = "members")
    @Mapping(target = "lastMessage", ignore = true)
    ChatDTO toDto(Chat chat);

    @Mapping(target = "chatId", source = "dto.chatId")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "groupChat", source = "dto.groupChat")
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "messages", ignore = true)
    Chat toEntity(ChatDTO dto);
}
