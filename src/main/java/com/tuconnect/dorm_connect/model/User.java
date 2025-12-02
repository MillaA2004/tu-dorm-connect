package com.tuconnect.dorm_connect.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "image_url", nullable = true)
    private String profileImageUrl;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private Integer academicYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Roles role;


    @ManyToOne
    @JoinColumn(name = "dorm_id")
    private Dorm dorm;

    @OneToMany(mappedBy = "user")
    private List<Listing> listings;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "creator")
    private List<Event> organizedEvents;

    @ManyToMany(mappedBy = "participants")
    private List<Event> events;
}
