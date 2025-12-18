package com.tuconnect.dorm_connect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"reported_entity_id", "reported_entity_type", "reporter_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reported_entity_id", nullable = false)
    private Long reportedEntityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reported_entity_type", nullable = false)
    private ReportTargetType reportedEntityType;

    @Column(nullable = false)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}