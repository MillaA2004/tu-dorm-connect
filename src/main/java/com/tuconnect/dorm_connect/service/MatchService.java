package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.UserMatch.UserMatchDTO;

import java.util.List;

public interface MatchService {
    List<UserMatchDTO> generateMatchesForViewer(Long viewerId, Double minScore);
    List<UserMatchDTO> generateAllMatches(Double minScore);
}
