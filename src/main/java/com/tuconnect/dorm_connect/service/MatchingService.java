package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.model.Questionnaire;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {
    public double calculateMatchScore(Questionnaire q1, Questionnaire q2) {
        if (q1 == null || q2 == null) return 0.0;

        double score = 0.0;
        int total = 0;

        // Boolean comparisons
        score += 2 * match(q1.getSmokes(), q2.getSmokes()); total++;
        score += 2 * match(q1.getDrinks(), q2.getDrinks()); total++;
        score += 3 * match(q1.getPartyHome(), q2.getPartyHome()); total++;
        score += match(q1.getStayAtHome(), q2.getStayAtHome()); total++;
        score += 2 * match(q1.getSharesCleaning(), q2.getSharesCleaning()); total++;
        score += match(q1.getEarlyRiser(), q2.getEarlyRiser()); total++;
        score += match(q1.getStudiesInRoom(), q2.getStudiesInRoom()); total++;
        score += match(q1.getPrefersSocialRoommate(), q2.getPrefersSocialRoommate()); total++;
        score += 3 * match(q1.getCooksInDorm(), q2.getCooksInDorm()); total++;
        score += match(q1.getUsesHeadphones(), q2.getUsesHeadphones()); total++;
        score += 2 * match(q1.getSharesItems(), q2.getSharesItems()); total++;

        // Scaled comparisons (1–5)
        score += 3 * scaleMatch(q1.getCleanliness(), q2.getCleanliness()); total++;
        score += 2 * scaleMatch(q1.getBedtime(), q2.getBedtime()); total++;
        score += 2 * scaleMatch(q1.getNeedsQuiet(), q2.getNeedsQuiet()); total++;
        score += 3 * scaleMatch(q1.getGuestFrequency(), q2.getGuestFrequency()); total++;
        score += scaleMatch(q1.getFoodSharing(), q2.getFoodSharing()); total++;
        score += scaleMatch(q1.getEntertainmentFrequency(), q2.getEntertainmentFrequency()); total++;
        score += 3 * scaleMatch(q1.getPersonalSpaceImportance(), q2.getPersonalSpaceImportance()); total++;

        // String comparisons
        score += stringMatch(q1.getMbti(), q2.getMbti()); total++;
        score += 0.5 * stringMatch(q1.getSpecialty(), q2.getSpecialty()); total++;

        // Age difference
        if (q1.getAge() != null && q2.getAge() != null) {
            score += 1 - (Math.abs(q1.getAge() - q2.getAge()) / 100.0);
            total++;
        }

        return (score / total) * 100;
    }

    private double match(Boolean a, Boolean b) {
        return (a != null && a.equals(b)) ? 1.0 : 0.0;
    }

    private double scaleMatch(Integer a, Integer b) {
        return (a != null && b != null) ? 1 - (Math.abs(a - b) / 4.0) : 0.0;
    }

    private double stringMatch(String a, String b) {
        return (a != null && a.equalsIgnoreCase(b)) ? 1.0 : 0.0;
    }
}

