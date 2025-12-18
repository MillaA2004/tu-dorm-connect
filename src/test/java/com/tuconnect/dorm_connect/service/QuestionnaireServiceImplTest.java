package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;
import com.tuconnect.dorm_connect.mapper.QuestionnaireMapper;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.implementations.QuestionnaireServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @Mock
    private QuestionnaireMapper questionnaireMapper;

    @InjectMocks
    private QuestionnaireServiceImpl questionnaireService;

    private User user;
    private Questionnaire questionnaire;
    private QuestionnaireDTO dto;

    @BeforeEach
    void setUp() {
        user = new User();
        User user = new User();
        user.setFirstName("Milla");
        user.setLastName("Angelova");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setAcademicYear(3);
        user.setMajor("ISN");
        user.setGender(User.Gender.FEMALE);
        user.setRole(Roles.User);

        questionnaire = new Questionnaire();
        questionnaire.setId(1L);
        questionnaire.setUser(user);
        questionnaire.setMbti("INTJ");
        questionnaire.setAge(21);
        questionnaire.setSpecialty("Computer Science");

        dto = new QuestionnaireDTO(
                true, false, true, true,
                4, true, "INTJ", 21, "Computer Science",
                true, 2,
                true, 3,
                2, true,
                true, 3,
                2, true,
                4, true
        );
    }

    @Test
    void saveForUser_shouldSaveAndReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUser(user)).thenReturn(Optional.empty());
        when(questionnaireRepository.save(any(Questionnaire.class))).thenReturn(questionnaire);
        when(questionnaireMapper.toDto(any(Questionnaire.class))).thenReturn(dto);

        QuestionnaireDTO result = questionnaireService.saveForUser(1L, dto);

        assertThat(result.mbti()).isEqualTo("INTJ");
        assertThat(result.age()).isEqualTo(21);
        verify(questionnaireRepository).save(any(Questionnaire.class));
    }

    @Test
    void getByUser_shouldReturnDto() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUser(user)).thenReturn(Optional.of(questionnaire));
        when(questionnaireMapper.toDto(questionnaire)).thenReturn(dto);

        // Act
        QuestionnaireDTO result = questionnaireService.getByUser(1L);

        // Assert
        assertThat(result.mbti()).isEqualTo("INTJ");
        assertThat(result.specialty()).isEqualTo("Computer Science");
    }

    @Test
    void saveForUser_shouldThrowIfUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionnaireService.saveForUser(99L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getByUser_shouldThrowIfQuestionnaireNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionnaireService.getByUser(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Questionnaire not found");
    }
}


