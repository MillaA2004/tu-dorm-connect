package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;
import com.tuconnect.dorm_connect.security.JwtAuthenticationFilter;
import com.tuconnect.dorm_connect.service.QuestionnaireService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = QuestionnaireController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class QuestionnaireControllerMvcTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private QuestionnaireService questionnaireService;

    @Test
    void createOrUpdate_shouldReturnSavedQuestionnaire() throws Exception {
        Long userId = 1L;
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

        when(questionnaireService.saveForUser(eq(userId), any(QuestionnaireDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/questionnaires/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mbti").value("INTJ"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.specialty").value("Computer Science"));
    }
}
