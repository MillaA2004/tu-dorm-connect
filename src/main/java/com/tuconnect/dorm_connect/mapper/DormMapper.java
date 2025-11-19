package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.DormDTO;
import com.tuconnect.dorm_connect.model.Dorm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DormMapper {

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "address", source = "dto.address")
    @Mapping(target = "blockNumber", source = "dto.blockNumber")
    @Mapping(target = "amenitiesJson", source = "dto.amenitiesJson")
    @Mapping(target = "price", source = "dto.price")
    Dorm toEntity(DormDTO dto);

    @Mapping(target = "id", source = "dorm.id")
    @Mapping(target = "name", source = "dorm.name")
    @Mapping(target = "address", source = "dorm.address")
    @Mapping(target = "blockNumber", source = "dorm.blockNumber")
    @Mapping(target = "amenitiesJson", source = "dorm.amenitiesJson")
    @Mapping(target = "price", source = "dorm.price")
    DormDTO toDTO(Dorm dorm);
}
