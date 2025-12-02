package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Chat.ChatMemberDTO;

import com.tuconnect.dorm_connect.model.ChatMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMemberMapper {


    @Mapping(target = "chatMemberId", source = "chatMemberId")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "chatRole", source = "chatRole")
    ChatMemberDTO toDto(ChatMember member);


    @Mapping(target = "chatMemberId", ignore = true)
    @Mapping(target = "chat", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "chatRole", source = "chatRole")
    ChatMember toEntity(ChatMemberDTO dto);
}
