package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Questionnaire.QuestionnaireDTO;
import com.tuconnect.dorm_connect.service.QuestionnaireService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questionnaires")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @PostMapping("/{userId}")
    public ResponseEntity<QuestionnaireDTO> createOrUpdate(
            @PathVariable Long userId,
            @RequestBody QuestionnaireDTO questionnaireDTO) {
        QuestionnaireDTO saved = questionnaireService.saveForUser(userId, questionnaireDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<QuestionnaireDTO> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(questionnaireService.getByUser(userId));
    }
}
