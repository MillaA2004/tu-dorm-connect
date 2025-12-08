package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;
import com.tuconnect.dorm_connect.mapper.UserMatchMapper;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.model.UserMatch;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserMatchRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {

    private final UserRepository userRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final UserMatchRepository userMatchRepository;
    private final MatchingService matchingService;
    private final UserMatchMapper userMatchMapper;

    @GetMapping("/{viewerId}")
    public ResponseEntity<List<UserMatchDTO>> getMatches(@PathVariable Long viewerId) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viewer not found"));

        Questionnaire viewerQ = questionnaireRepository.findByUser(viewer)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Viewer has no questionnaire"));

        List<User> posters = userRepository.findByRole(Roles.Poster).stream()
                .filter(p -> p.getQuestionnaire() != null)
                .toList();

        List<UserMatchDTO> matches = posters.stream()
                .map(poster -> {
                    double score = matchingService.calculateMatchScore(viewerQ, poster.getQuestionnaire());
                    UserMatch match = UserMatch.builder()
                            .viewer(viewer)
                            .poster(poster)
                            .score(score)
                            .createdAt(LocalDateTime.now())
                            .build();
                    userMatchRepository.save(match);
                    return userMatchMapper.toDTO(match);
                })
                .sorted(Comparator.comparing(UserMatchDTO::score).reversed())
                .toList();

        return ResponseEntity.ok(matches);
    }
}
