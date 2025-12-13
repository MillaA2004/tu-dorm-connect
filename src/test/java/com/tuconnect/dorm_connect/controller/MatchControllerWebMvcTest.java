package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.dto.User.UserListingSummaryDTO;
import com.tuconnect.dorm_connect.security.CustomUserDetailsService;
import com.tuconnect.dorm_connect.security.JwtTokenProvider;
import com.tuconnect.dorm_connect.service.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.tuconnect.dorm_connect.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MatchController.class,
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class MatchControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void generateMatches_shouldReturn200AndJson() throws Exception {
        Long viewerId = 1L;

        UserListingSummaryDTO posterSummary = new UserListingSummaryDTO(
                2L, "Bob", "Poster", "ISN", "picture", "Block 14", 1
        );
        UserMatchDTO dto = new UserMatchDTO(posterSummary, 85.0);

        when(matchService.generateMatchesForViewer(viewerId, null)).thenReturn(List.of(dto));

        mockMvc.perform(get("/matches/1").accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].poster.id").value(2))
                .andExpect(jsonPath("$[0].poster.firstName").value("Bob"))
                .andExpect(jsonPath("$[0].poster.lastName").value("Poster"))
                .andExpect(jsonPath("$[0].poster.major").value("ISN"))
                .andExpect(jsonPath("$[0].poster.profileImageUrl").value("picture"))
                .andExpect(jsonPath("$[0].poster.dorm").value("Block 14"))
                .andExpect(jsonPath("$[0].poster.academicYear").value(1))
                .andExpect(jsonPath("$[0].score").value(85.0));
    }

    @Test
    void generateMatches_shouldReturnEmptyList_whenNoMatches() throws Exception {
        Long viewerId = 1L;

        when(matchService.generateMatchesForViewer(viewerId, null)).thenReturn(List.of());

        mockMvc.perform(get("/matches/1").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void generateMatches_shouldReturn404_whenViewerNotFound() throws Exception {
        when(matchService.generateMatchesForViewer(99L, null))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Viewer not found"));

        mockMvc.perform(get("/matches/99").accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void generateMatches_shouldReturn400_whenViewerHasNoQuestionnaire() throws Exception {
        when(matchService.generateMatchesForViewer(1L, null))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Viewer has no questionnaire"));

        mockMvc.perform(get("/matches/1").accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateMatches_withCustomMinScore_shouldReturnFilteredResults() throws Exception {
        Long viewerId = 1L;
        UserMatchDTO dto = new UserMatchDTO(
                new UserListingSummaryDTO(2L, "Bob", "Poster", "ISN", "picture", "Block 14", 1), 85.0
        );

        when(matchService.generateMatchesForViewer(viewerId, 75.0)).thenReturn(List.of(dto));

        mockMvc.perform(get("/matches/1?minScore=75").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(85.0));
    }
}