package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchingServiceTest {

    private final MatchingService matchingService = new MatchingService();

    @Test
    void calculateMatchScore_shouldReturn100_whenQuestionnairesAreIdentical() {
        Questionnaire q1 = createSampleQuestionnaire();
        Questionnaire q2 = createSampleQuestionnaire();

        double score = matchingService.calculateMatchScore(q1, q2);

        assertThat(score).isEqualTo(100.0);
    }

    @Test
    void calculateMatchScore_shouldReturnZero_whenQuestionnairesAreNull() {
        double score = matchingService.calculateMatchScore(null, null);
        assertThat(score).isEqualTo(0.0);
    }

    @Test
    void calculateMatchScore_shouldHandlePartialMatches() {
        Questionnaire q1 = createSampleQuestionnaire();
        Questionnaire q2 = createSampleQuestionnaire();
        q2.setSmokes(false); // mismatch
        q2.setDrinks(true);  // mismatch
        q2.setCleanliness(1); // scaled mismatch

        double score = matchingService.calculateMatchScore(q1, q2);

        assertThat(score).isLessThan(100.0);
        assertThat(score).isGreaterThan(0.0);
    }

    private Questionnaire createSampleQuestionnaire() {
        Questionnaire q = new Questionnaire();
        q.setSmokes(true);
        q.setDrinks(false);
        q.setPartyHome(true);
        q.setStayAtHome(true);
        q.setSharesCleaning(true);
        q.setEarlyRiser(true);
        q.setStudiesInRoom(true);
        q.setPrefersSocialRoommate(true);
        q.setCooksInDorm(true);
        q.setUsesHeadphones(true);
        q.setSharesItems(true);
        q.setCleanliness(4);
        q.setBedtime(2);
        q.setNeedsQuiet(3);
        q.setGuestFrequency(2);
        q.setFoodSharing(3);
        q.setEntertainmentFrequency(2);
        q.setPersonalSpaceImportance(4);
        q.setMbti("INTJ");
        q.setSpecialty("Computer Science");
        q.setAge(21);
        return q;
    }
}

