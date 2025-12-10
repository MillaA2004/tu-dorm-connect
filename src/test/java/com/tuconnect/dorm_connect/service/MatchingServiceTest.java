package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.mapper.UserMatchMapper;
import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.model.UserMatch;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserMatchRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@EntityScan("com.tuconnect.dorm_connect.model")
class MatchServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private QuestionnaireRepository questionnaireRepository;
    @Mock
    private UserMatchRepository userMatchRepository;
    @Mock
    private MatchingService matchingService;
    @Mock
    private UserMatchMapper userMatchMapper;

    @InjectMocks
    private MatchService matchService;

    @Test
    void generateMatchesForViewer_shouldReturnSortedMatches() {
        // Arrange
        Long viewerId = 1L;
        User viewer = new User();
        viewer.setId(viewerId);

        Questionnaire viewerQ = new Questionnaire();
        viewerQ.setUser(viewer);

        User poster = new User();
        poster.setId(2L);
        Questionnaire posterQ = new Questionnaire();
        posterQ.setUser(poster);
        poster.setQuestionnaire(posterQ);
        poster.setListings(List.of(new Listing())); // poster has listings

        UserMatch match = UserMatch.builder()
                .viewer(viewer)
                .poster(poster)
                .score(85.0)
                .createdAt(LocalDateTime.now())
                .build();

        UserMatchDTO dto = new UserMatchDTO(null, 85.0);

        when(userRepository.findById(viewerId)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.of(viewerQ));
        when(userRepository.findAll()).thenReturn(List.of(viewer, poster));
        when(matchingService.calculateMatchScore(viewerQ, posterQ)).thenReturn(85.0);
        when(userMatchMapper.toDTO(any(UserMatch.class))).thenReturn(dto);

        // Act
        List<UserMatchDTO> result = matchService.generateMatchesForViewer(viewerId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(85.0, result.get(0).score());
        verify(userMatchRepository).deleteByViewer(viewer);
        verify(userMatchRepository).saveAll(anyList());
    }

    @Test
    void generateMatchesForViewer_shouldThrowIfViewerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> matchService.generateMatchesForViewer(1L));
    }

    @Test
    void generateMatchesForViewer_shouldThrowIfNoQuestionnaire() {
        User viewer = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(questionnaireRepository.findByUser(viewer)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> matchService.generateMatchesForViewer(1L));
    }

}
