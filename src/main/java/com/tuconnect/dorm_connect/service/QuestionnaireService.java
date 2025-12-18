package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;

public interface QuestionnaireService {
    QuestionnaireDTO saveForUser(Long userId, QuestionnaireDTO dto);
    QuestionnaireDTO getByUser(Long userId);
}
