package com.tuconnect.dorm_connect.model;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String title;

    @Column(length = 2000)
    private String description;

    private String address;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    private Integer capacity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "event_type")
    private String eventType;

    private Double latitude;

    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    @ManyToOne(optional = true)
    @JoinColumn(name = "chat_id")
    private Chat chat;
}
