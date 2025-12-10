package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.mapper.UserMatchMapper;
import com.tuconnect.dorm_connect.model.*;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserMatchRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository userRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final UserMatchRepository userMatchRepository;
    private final MatchingService matchingService;
    private final UserMatchMapper userMatchMapper;

    private static final double MIN_SCORE = 60.0;

    public List<UserMatchDTO> generateMatchesForViewer(Long viewerId) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Viewer not found"));

        Questionnaire viewerQ = questionnaireRepository.findByUser(viewer)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Viewer has no questionnaire"));

        userMatchRepository.deleteByViewer(viewer);

        List<User> posters = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(viewer.getId()))
                .filter(u -> u.getQuestionnaire() != null && !u.getListings().isEmpty())
                .toList();



        List<UserMatch> matches = posters.stream()
                .map(poster -> {
                    double score = matchingService.calculateMatchScore(viewerQ, poster.getQuestionnaire());
                    return UserMatch.builder()
                            .viewer(viewer)
                            .poster(poster)
                            .score(score)
                            .createdAt(LocalDateTime.now())
                            .build();
                })
                .filter(match -> match.getScore() >= MIN_SCORE)
                .toList();

        userMatchRepository.saveAll(matches);

        return matches.stream()
                .sorted(Comparator.comparing(UserMatch::getScore).reversed())
                .map(userMatchMapper::toDTO)
                .collect(Collectors.toList());
    }
}
