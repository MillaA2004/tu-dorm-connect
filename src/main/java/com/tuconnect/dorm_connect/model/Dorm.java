package com.tuconnect.dorm_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Entity
@Data
@Table(name = "dorm")
@NoArgsConstructor
@AllArgsConstructor
public class Dorm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @org.hibernate.mapping.OneToMany(mappedBy = "dorm")
    private List<Review> reviews;
}
