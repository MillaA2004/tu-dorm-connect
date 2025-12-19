package com.tuconnect.dorm_connect.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.QuestionnaireService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class QuestionnaireIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private QuestionnaireRepository questionnaireRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        questionnaireRepository.deleteAll();

        savedUser = userRepository.save(User.builder()
                .firstName("Milla")
                .lastName("Angelova")
                .email("test@example.com")
                .password("password")
                .major("ISN")
                .academicYear(3)
                .gender(User.Gender.FEMALE)
                .role(Roles.User)
                .build());
    }

    @Test
    void createOrUpdate_shouldPersistQuestionnaire() throws Exception {
        QuestionnaireDTO dto = new QuestionnaireDTO(
                true, false, true, true,
                4, true, "INTJ", 21, "Computer Science",
                true, 2,
                true, 3,
                2, true,
                true, 3,
                2, true,
                4, true
        );

        mockMvc.perform(post("/questionnaires/{userId}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mbti").value("INTJ"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.specialty").value("Computer Science"));

        Questionnaire saved = questionnaireRepository.findByUser(savedUser).orElseThrow();
        assertThat(saved.getMbti()).isEqualTo("INTJ");
        assertThat(saved.getAge()).isEqualTo(21);
    }
    @Test
    void getByUser_shouldReturnPersistedQuestionnaire() throws Exception {
        Questionnaire q = new Questionnaire();
        q.setUser(savedUser);
        q.setMbti("INTJ");
        q.setAge(21);
        q.setSpecialty("Computer Science");
        questionnaireRepository.save(q);
        mockMvc.perform(get("/questionnaires/{userId}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mbti").value("INTJ"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.specialty").value("Computer Science"));
    }
}
