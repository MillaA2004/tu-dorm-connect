package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {
    Optional<Questionnaire> findByUser(User user);

    Questionnaire findByUserId(@NotNull(message = "User ID is required") Long aLong);
}
