package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;
import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.mapper.UserMatchMapper;
import com.tuconnect.dorm_connect.model.*;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserMatchRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.MatchService;
import com.tuconnect.dorm_connect.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(MatchController.class)
class MatchControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private QuestionnaireRepository questionnaireRepository;

    @MockBean
    private UserMatchRepository userMatchRepository;

    @MockBean
    private MatchingService matchingService;

    @MockBean
    private UserMatchMapper userMatchMapper;

    @Test
    void generateMatches_shouldReturn200AndJson() throws Exception {
        // Arrange
        Long viewerId = 1L;
        User viewer = new User();
        viewer.setId(viewerId);

        Questionnaire viewerQ = new Questionnaire();
        viewerQ.setSmokes(true);
        viewerQ.setDrinks(false);
        viewerQ.setPartyHome(true);
        viewerQ.setStayAtHome(true);
        viewerQ.setSharesCleaning(true);
        viewerQ.setEarlyRiser(true);
        viewerQ.setStudiesInRoom(true);
        viewerQ.setPrefersSocialRoommate(true);
        viewerQ.setCooksInDorm(true);
        viewerQ.setUsesHeadphones(true);
        viewerQ.setSharesItems(true);
        viewerQ.setCleanliness(4);
        viewerQ.setBedtime(2);
        viewerQ.setNeedsQuiet(3);
        viewerQ.setGuestFrequency(2);
        viewerQ.setFoodSharing(3);
        viewerQ.setEntertainmentFrequency(2);
        viewerQ.setPersonalSpaceImportance(4);
        viewerQ.setMbti("INTJ");
        viewerQ.setSpecialty("Computer Science");
        viewerQ.setAge(21);
        viewerQ.setUser(viewer);

        User poster = new User();
        poster.setId(2L);
        Questionnaire posterQ = new Questionnaire();
        posterQ.setSmokes(true);
        posterQ.setDrinks(false);
        posterQ.setPartyHome(true);
        posterQ.setStayAtHome(true);
        posterQ.setSharesCleaning(true);
        posterQ.setEarlyRiser(true);
        posterQ.setStudiesInRoom(true);
        posterQ.setPrefersSocialRoommate(true);
        posterQ.setCooksInDorm(true);
        posterQ.setUsesHeadphones(true);
        posterQ.setSharesItems(true);
        posterQ.setCleanliness(4);
        posterQ.setBedtime(2);
        posterQ.setNeedsQuiet(3);
        posterQ.setGuestFrequency(2);
        posterQ.setFoodSharing(3);
        posterQ.setEntertainmentFrequency(2);
        posterQ.setPersonalSpaceImportance(4);
        posterQ.setMbti("INTJ");
        posterQ.setSpecialty("Computer Science");
        posterQ.setAge(21);
        poster.setQuestionnaire(posterQ);
        poster.setListings(List.of(new Listing()));


        UserMatch match = UserMatch.builder()
                .viewer(viewer)
                .poster(poster)
                .score(85.0)
                .createdAt(LocalDateTime.now())
                .build();

        UserSummaryDTO posterSummary = new UserSummaryDTO(
                2L, "Bob", "Poster", "ISN", "picture", "Block 14", 1
        );
        UserMatchDTO dto = new UserMatchDTO(posterSummary, 85.0);

        when(userRepository.findById(viewerId)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.of(viewerQ));
        when(userRepository.findAll()).thenReturn(List.of(poster));
        when(matchingService.calculateMatchScore(viewerQ, posterQ)).thenReturn(85.0);
        when(userMatchRepository.save(any(UserMatch.class))).thenReturn(match);
        when(userMatchMapper.toDTO(any(UserMatch.class))).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/matches/1")
                        .accept(MediaType.APPLICATION_JSON))
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
}
