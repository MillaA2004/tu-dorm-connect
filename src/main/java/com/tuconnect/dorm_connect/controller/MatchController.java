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
import com.tuconnect.dorm_connect.service.MatchService;
import com.tuconnect.dorm_connect.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/{viewerId}")
    public ResponseEntity<List<UserMatchDTO>> getMatches(
            @PathVariable Long viewerId,
            @RequestParam(required = false) Double minScore) {
        return ResponseEntity.ok(matchService.generateMatchesForViewer(viewerId, minScore));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserMatchDTO>> getAllMatches(
            @RequestParam(required = false) Double minScore) {
        return ResponseEntity.ok(matchService.generateAllMatches(minScore));
    }
}
