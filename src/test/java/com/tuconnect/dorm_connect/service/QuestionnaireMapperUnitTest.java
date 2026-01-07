package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;
import com.tuconnect.dorm_connect.mapper.QuestionnaireMapper;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionnaireMapperUnitTest {

    private final QuestionnaireMapper mapper = Mappers.getMapper(QuestionnaireMapper.class);

    @Test
    void toDTO_shouldMapAllFieldsCorrectly() {
        User user = new User();
        user.setId(123L);

        Questionnaire entity = new Questionnaire();
        entity.setId(1L);
        entity.setUser(user);
        entity.setAge(22);
        entity.setMbti("ENFP");
        entity.setCleanliness(5);
        entity.setSmokes(false);

        QuestionnaireDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.age()).isEqualTo(22);
        assertThat(dto.mbti()).isEqualTo("ENFP");
        assertThat(dto.cleanliness()).isEqualTo(5);
        assertThat(dto.smokes()).isFalse();
    }

    @Test
    void toEntity_shouldMapAllFieldsCorrectly() {
        Questionnaire entity = new Questionnaire();
        entity.setId(1L);
        entity.setUser(new User());
        entity.setAge(25);
        entity.setMbti("INTJ");
        entity.setCleanliness(3);
        entity.setDrinks(true);

        QuestionnaireDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.age()).isEqualTo(25);
        assertThat(dto.mbti()).isEqualTo("INTJ");
        assertThat(dto.cleanliness()).isEqualTo(3);
        assertThat(dto.drinks()).isTrue();
    }
}