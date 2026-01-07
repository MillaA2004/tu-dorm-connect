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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MatchServiceImplExtendedTest {

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

    private User createValidUser(Long id, User.Gender gender, boolean isPoster) {
        User user = new User();
        user.setId(id);
        user.setGender(gender);
        Questionnaire q = new Questionnaire();
        user.setQuestionnaire(q);
        
        if (isPoster) {
            Listing listing = new Listing();
            listing.setIsActive(true);
            listing.setExpiresAt(LocalDateTime.now().plusDays(10));
            user.setListings(List.of(listing));
        } else {
            user.setListings(Collections.emptyList());
        }
        return user;
    }

    @Test
    void generateMatchesForViewer_shouldThrowException_whenViewerNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.generateMatchesForViewer(99L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Viewer not found");
    }

    @Test
    void generateMatchesForViewer_shouldReturnEmptyList_whenViewerHasNoQuestionnaire() {
        User viewer = new User();
        viewer.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.empty());

        List<UserMatchDTO> result = matchService.generateMatchesForViewer(1L, null);

        assertThat(result).isEmpty();
    }

    @Test
    void generateAllMatches_shouldGenerateCorrectMatchesAcrossAllUsers() {
        User viewer = createValidUser(1L, User.Gender.FEMALE, false);
        User poster = createValidUser(2L, User.Gender.FEMALE, true);
        User malePoster = createValidUser(3L, User.Gender.MALE, true);

        when(userRepository.findByQuestionnaireIsNotNull()).thenReturn(List.of(viewer, poster, malePoster));
        when(userRepository.findByQuestionnaireIsNotNullAndListingsIsNotEmpty()).thenReturn(List.of(poster, malePoster));
        
        when(matchingService.calculateMatchScore(any(), any())).thenReturn(75.0);
        
        UserMatchDTO dto = new UserMatchDTO(
                new UserListingSummaryDTO(2L, "Alice", "Smith", "CS", "img", 1),
                75.0
        );
        when(userMatchMapper.toDTO(any(UserMatch.class))).thenReturn(dto);

        List<UserMatchDTO> result = matchService.generateAllMatches(60.0);

        // Viewer(1, Female) matches Poster(2, Female). 
        // Poster(2, Female) matches no one (no other female posters).
        // MalePoster(3) matches no one.
        assertThat(result).hasSize(1);
        verify(userMatchRepository).deleteAll();
        verify(userMatchRepository).saveAll(anyList());
    }

    @Test
    void generateAllMatches_shouldFilterExpiredListings() {
        User viewer = createValidUser(1L, User.Gender.MALE, false);
        User posterWithExpiredListing = createValidUser(2L, User.Gender.MALE, true);
        posterWithExpiredListing.getListings().get(0).setExpiresAt(LocalDateTime.now().minusDays(1));

        when(userRepository.findByQuestionnaireIsNotNull()).thenReturn(List.of(viewer));
        when(userRepository.findByQuestionnaireIsNotNullAndListingsIsNotEmpty()).thenReturn(List.of(posterWithExpiredListing));

        List<UserMatchDTO> result = matchService.generateAllMatches(null);

        assertThat(result).isEmpty();
    }
}