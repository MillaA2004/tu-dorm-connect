package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Dorm.DormRequestDTO;
import com.tuconnect.dorm_connect.dto.Dorm.DormResponseDTO;
import com.tuconnect.dorm_connect.model.Dorm;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DormMapper {

    Dorm toEntity(DormRequestDTO dto);

    DormResponseDTO toDTO(Dorm dorm);
}
