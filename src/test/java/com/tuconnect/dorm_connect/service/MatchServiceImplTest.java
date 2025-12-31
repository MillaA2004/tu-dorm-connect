package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.dto.User.UserListingSummaryDTO;
import com.tuconnect.dorm_connect.mapper.UserMatchMapper;
import com.tuconnect.dorm_connect.model.*;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserMatchRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.implementations.MatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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

    private User createValidPoster(Long id, User.Gender gender) {
        User poster = new User();
        poster.setId(id);
        poster.setGender(gender);

        Questionnaire q = new Questionnaire();
        poster.setQuestionnaire(q);

        Listing listing = new Listing();
        listing.setIsActive(true);
        listing.setExpiresAt(LocalDateTime.now().plusDays(1));

        poster.setListings(List.of(listing));
        return poster;
    }

    @Test
    void generateMatchesForViewer_shouldReturnMatchesAboveThreshold() {
        User viewer = new User();
        viewer.setId(1L);
        viewer.setGender(User.Gender.MALE);
        Questionnaire viewerQ = new Questionnaire();
        viewerQ.setUser(viewer);

        User poster = createValidPoster(2L, User.Gender.MALE);

        UserMatchDTO dto = new UserMatchDTO(
                new UserListingSummaryDTO(2L, "Bob", "Poster", "ISN", "pic", 2),
                85.0
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.of(viewerQ));
        when(userRepository.findByQuestionnaireIsNotNullAndListingsIsNotEmpty())
                .thenReturn(List.of(poster));

        when(matchingService.calculateMatchScore(any(), any())).thenReturn(85.0);
        when(userMatchMapper.toDTO(any(UserMatch.class))).thenReturn(dto);

        List<UserMatchDTO> result = matchService.generateMatchesForViewer(1L, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).score()).isEqualTo(85.0);
        verify(userMatchRepository).deleteByViewer(viewer);
        verify(userMatchRepository).saveAll(anyList());
    }

    @Test
    void generateMatchesForViewer_shouldReturnEmptyList_whenDifferentGender() {
        // Arrange
        User viewer = new User();
        viewer.setId(1L);
        viewer.setGender(User.Gender.FEMALE);
        Questionnaire viewerQ = new Questionnaire();
        viewerQ.setUser(viewer);

        User poster = createValidPoster(2L, User.Gender.MALE); // Different Gender

        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.of(viewerQ));
        when(userRepository.findByQuestionnaireIsNotNullAndListingsIsNotEmpty())
                .thenReturn(List.of(poster));

        List<UserMatchDTO> result = matchService.generateMatchesForViewer(1L, null);

        assertThat(result).isEmpty();
    }

    @Test
    void generateMatchesForViewer_shouldReturnEmptyList_whenNoMatchesAboveThreshold() {
        User viewer = new User();
        viewer.setId(1L);
        viewer.setGender(User.Gender.MALE);
        Questionnaire viewerQ = new Questionnaire();
        viewerQ.setUser(viewer);

        User poster = createValidPoster(2L, User.Gender.MALE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.of(viewerQ));
        when(userRepository.findByQuestionnaireIsNotNullAndListingsIsNotEmpty())
                .thenReturn(List.of(poster));

        when(matchingService.calculateMatchScore(any(), any())).thenReturn(30.0);

        List<UserMatchDTO> result = matchService.generateMatchesForViewer(1L, null);

        assertThat(result).isEmpty();
    }
}