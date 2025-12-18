package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;
import com.tuconnect.dorm_connect.model.Questionnaire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class QuestionnaireMapperTest {

    private final QuestionnaireMapper mapper = Mappers.getMapper(QuestionnaireMapper.class);

    @Test
    void toDto_shouldMapEntityToDto() {
        Questionnaire q = new Questionnaire();
        q.setMbti("INTJ");
        q.setAge(21);
        q.setSpecialty("Computer Science");

        QuestionnaireDTO dto = mapper.toDto(q);

        assertThat(dto.mbti()).isEqualTo("INTJ");
        assertThat(dto.age()).isEqualTo(21);
        assertThat(dto.specialty()).isEqualTo("Computer Science");
    }
}

