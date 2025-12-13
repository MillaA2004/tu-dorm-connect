package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.mapper.UserMatchMapper;
import com.tuconnect.dorm_connect.model.*;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserMatchRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.MatchService;
import com.tuconnect.dorm_connect.service.MatchingService;
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

    public List<UserMatchDTO> generateMatchesForViewer(Long viewerId, Double minScore) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Viewer not found"));

        Questionnaire viewerQ = questionnaireRepository.findByUser(viewer)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Viewer has no questionnaire"));

        double threshold = (minScore != null) ? minScore : MIN_SCORE;

        userMatchRepository.deleteByViewer(viewer);

        List<User> posters = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(viewer.getId()))
                .filter(u -> u.getQuestionnaire() != null && !u.getListings().isEmpty())
                .toList();



        List<UserMatch> matches = posters.stream()
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

    public List<UserMatchDTO> generateAllMatches(Double minScore) {
        double threshold = (minScore != null) ? minScore : MIN_SCORE;

        userMatchRepository.deleteAll();

        List<User> allUsers = userRepository.findAll();

        List<User> viewers = allUsers.stream()
                .filter(u -> u.getQuestionnaire() != null)
                .toList();

        List<User> posters = allUsers.stream()
                .filter(u -> u.getQuestionnaire() != null && !u.getListings().isEmpty())
                .toList();

        List<UserMatch> allMatches = viewers.stream()
                .flatMap(viewer -> posters.stream()
                        .filter(poster -> !viewer.getId().equals(poster.getId()))
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
