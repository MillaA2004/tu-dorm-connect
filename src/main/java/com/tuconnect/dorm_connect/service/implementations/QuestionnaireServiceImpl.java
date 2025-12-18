package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;
import com.tuconnect.dorm_connect.mapper.QuestionnaireMapper;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final UserRepository userRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionnaireMapper questionnaireMapper;

    @Override
    public QuestionnaireDTO saveForUser(Long userId, QuestionnaireDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Questionnaire questionnaire = questionnaireRepository.findByUser(user)
                .orElse(new Questionnaire());

        questionnaireMapper.updateEntityFromDto(dto, questionnaire);
        questionnaire.setUser(user);

        Questionnaire saved = questionnaireRepository.save(questionnaire);
        return questionnaireMapper.toDto(saved);
    }

    @Override
    public QuestionnaireDTO getByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Questionnaire questionnaire = questionnaireRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questionnaire not found"));

        return questionnaireMapper.toDto(questionnaire);
    }
}
