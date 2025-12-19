package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Dorm.DormRequestDTO;
import com.tuconnect.dorm_connect.dto.Dorm.DormResponseDTO;
import com.tuconnect.dorm_connect.model.Dorm;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DormMapper {

    // Request → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Dorm toEntity(DormRequestDTO dto);

    // Entity → Response
    DormResponseDTO toDTO(Dorm dorm);
}
