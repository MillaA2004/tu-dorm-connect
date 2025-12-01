package com.tuconnect.dorm_connect.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Data
@Setter
@Getter
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @Column(nullable = false, length = 2000)
    private String content;

    private Instant sentAt = Instant.now();
}

