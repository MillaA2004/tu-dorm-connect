package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.dto.User.UserListingSummaryDTO;
import com.tuconnect.dorm_connect.mapper.UserMatchMapper;
import com.tuconnect.dorm_connect.model.*;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserMatchRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.ServiceImpl.MatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MatchServiceImplTest {

    private UserRepository userRepository;
    private QuestionnaireRepository questionnaireRepository;
    private UserMatchRepository userMatchRepository;
    private MatchingService matchingService;
    private UserMatchMapper userMatchMapper;
    private MatchServiceImpl matchService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        questionnaireRepository = mock(QuestionnaireRepository.class);
        userMatchRepository = mock(UserMatchRepository.class);
        matchingService = mock(MatchingService.class);
        userMatchMapper = mock(UserMatchMapper.class);

        matchService = new MatchServiceImpl(
                userRepository,
                questionnaireRepository,
                userMatchRepository,
                matchingService,
                userMatchMapper
        );
    }

    @Test
    void generateMatchesForViewer_shouldReturnMatchesAboveThreshold() {
        // Arrange
        User viewer = new User();
        viewer.setId(1L);
        Questionnaire viewerQ = new Questionnaire();
        viewerQ.setUser(viewer);

        User poster = new User();
        poster.setId(2L);
        Questionnaire posterQ = new Questionnaire();
        poster.setQuestionnaire(posterQ);
        poster.setListings(List.of(new Listing()));

        UserMatch match = UserMatch.builder()
                .viewer(viewer)
                .poster(poster)
                .score(85.0)
                .build();

        UserMatchDTO dto = new UserMatchDTO(
                new UserListingSummaryDTO(2L, "Bob", "Poster", "ISN", "pic", "Dorm", 2),
                85.0
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.of(viewerQ));
        when(userRepository.findAll()).thenReturn(List.of(viewer, poster));
        when(matchingService.calculateMatchScore(viewerQ, posterQ)).thenReturn(85.0);
        when(userMatchMapper.toDTO(any(UserMatch.class))).thenReturn(dto);

        // Act
        List<UserMatchDTO> result = matchService.generateMatchesForViewer(1L, null);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).score()).isEqualTo(85.0);
        verify(userMatchRepository).deleteByViewer(viewer);
        verify(userMatchRepository).saveAll(anyList());
    }

    @Test
    void generateMatchesForViewer_shouldReturnEmptyList_whenNoMatchesAboveThreshold() {
        User viewer = new User();
        viewer.setId(1L);
        Questionnaire viewerQ = new Questionnaire();
        viewerQ.setUser(viewer);

        User poster = new User();
        poster.setId(2L);
        Questionnaire posterQ = new Questionnaire();
        poster.setQuestionnaire(posterQ);
        poster.setListings(List.of(new Listing()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.of(viewerQ));
        when(userRepository.findAll()).thenReturn(List.of(viewer, poster));
        when(matchingService.calculateMatchScore(viewerQ, posterQ)).thenReturn(30.0); // below threshold

        List<UserMatchDTO> result = matchService.generateMatchesForViewer(1L, null);

        assertThat(result).isEmpty();
        verify(userMatchRepository).deleteByViewer(viewer);
        verify(userMatchRepository).saveAll(List.of());
    }
}

