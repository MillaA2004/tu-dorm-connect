package com.tuconnect.dorm_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "dorms")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dorm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    private String description;

    @Column(nullable = false)
    private Double price;

    @Column
    private List<String> imageUrlsList;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @OneToMany(mappedBy = "dorm")
    private List<Review> reviews;

    @OneToMany(mappedBy = "dorm")
    private List<Listing> listings;
}
