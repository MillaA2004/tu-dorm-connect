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

    @Column(name = "block_number")
    private String blockNumber;

    @Column(name = "amenities_json", columnDefinition = "TEXT")
    private String amenitiesJson;

    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "dorm")
    private List<Review> reviews;

    @OneToMany(mappedBy = "dorm")
    private List<User> livingPeople;

    @OneToMany(mappedBy = "dorm")
    private List<Listing> listings;
}
