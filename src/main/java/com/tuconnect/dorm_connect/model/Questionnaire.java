package com.tuconnect.dorm_connect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questionnaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    // Lifestyle & Habits
    private Boolean smokes;
    private Boolean drinks;
    private Boolean partyHome;
    private Boolean stayAtHome;

    // Cleanliness
    private Integer cleanliness; // 1–5
    private Boolean sharesCleaning;

    // MBTI, Age, Specialty
    private String mbti;
    private Integer age;
    private String specialty;

    // Daily Routine
    private Boolean earlyRiser;
    private Integer bedtime; // 1 = very early, 5 = very late

    // Study Habits
    private Boolean studiesInRoom;
    private Integer needsQuiet; // 1 = not at all, 5 = absolutely

    // Social Preferences
    private Integer guestFrequency; // 1 = never, 5 = very often
    private Boolean prefersSocialRoommate;

    // Food & Eating
    private Boolean cooksInDorm;
    private Integer foodSharing; // 1 = never, 5 = always

    // Noise & Entertainment
    private Integer entertainmentFrequency; // 1 = never, 5 = very often
    private Boolean usesHeadphones;

    // Boundaries
    private Integer personalSpaceImportance; // 1 = not important, 5 = extremely important
    private Boolean sharesItems;
}
