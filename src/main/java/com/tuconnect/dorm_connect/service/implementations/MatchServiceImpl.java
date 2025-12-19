package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.mapper.UserMatchMapper;
import com.tuconnect.dorm_connect.model.*;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserMatchRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.MatchService;
import com.tuconnect.dorm_connect.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchServiceImpl implements MatchService {

    private final UserRepository userRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final UserMatchRepository userMatchRepository;
    private final MatchingService matchingService;
    private final UserMatchMapper userMatchMapper;

    private static final double MIN_SCORE = 60.0;

    private UserMatch createMatch(User viewer, User poster, double score) {
        return UserMatch.builder()
                .viewer(viewer)
                .poster(poster)
                .score(score)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private double resolveThreshold(Double minScore) {
        return (minScore != null) ? minScore : MIN_SCORE;
    }

    @Override
    public List<UserMatchDTO> generateMatchesForViewer(Long viewerId, Double minScore) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viewer not found"));

        Questionnaire viewerQ = questionnaireRepository.findByUser(viewer).orElse(null);
        if (viewerQ == null) {
            return Collections.emptyList();
        }

        double threshold = resolveThreshold(minScore);

        userMatchRepository.deleteByViewer(viewer);

        List<User> posters = userRepository.findByQuestionnaireIsNotNullAndListingsIsNotEmpty();

        List<UserMatch> matches = posters.stream()
                .filter(poster -> !poster.getId().equals(viewer.getId()))
                .filter(poster -> poster.getGender() == viewer.getGender())
                .map(poster -> {
                    double score = matchingService.calculateMatchScore(viewerQ, poster.getQuestionnaire());
                    return createMatch(viewer, poster, score);
                })
                .filter(match -> match.getScore() >= threshold)
                .toList();

        userMatchRepository.saveAll(matches);

        return matches.stream()
                .sorted(Comparator.comparing(UserMatch::getScore).reversed())
                .map(userMatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserMatchDTO> generateAllMatches(Double minScore) {
        double threshold = resolveThreshold(minScore);

        userMatchRepository.deleteAll();

        List<User> viewers = userRepository.findByQuestionnaireIsNotNull();
        List<User> posters = userRepository.findByQuestionnaireIsNotNullAndListingsIsNotEmpty();

        List<UserMatch> allMatches = viewers.stream()
                .flatMap(viewer -> posters.stream()
                        .filter(poster -> !viewer.getId().equals(poster.getId()))
                        // prevent duplicate (viewer,poster) and (poster,viewer)
                        .filter(poster -> poster.getGender() == viewer.getGender())
                        .filter(poster -> viewer.getId() < poster.getId())
                        .map(poster -> {
                            double score = matchingService.calculateMatchScore(
                                    viewer.getQuestionnaire(), poster.getQuestionnaire()
                            );
                            return createMatch(viewer, poster, score);
                        })
                        .filter(match -> match.getScore() >= threshold)
                )
                .toList();

        userMatchRepository.saveAll(allMatches);

        return allMatches.stream()
                .sorted(Comparator.comparing(UserMatch::getScore).reversed())
                .map(userMatchMapper::toDTO)
                .collect(Collectors.toList());
    }
}
