package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;
import com.tuconnect.dorm_connect.model.Questionnaire;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface QuestionnaireMapper {

    QuestionnaireDTO toDto(Questionnaire entity);

    Questionnaire toEntity(QuestionnaireDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(QuestionnaireDTO dto, @MappingTarget Questionnaire entity);
}

