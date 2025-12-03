package com.tuconnect.dorm_connect.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viewer_id", nullable = false)
    private User viewer;

    @ManyToOne
    @JoinColumn(name = "poster_id", nullable = false)
    private User poster;

    // Match score between 0.0 and 100.0
    @Column(nullable = false)
    private Double score;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
