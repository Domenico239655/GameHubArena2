package com.gamehub.arena.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class EloHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne User user;

    private int oldElo;
    private int newElo;

    private Long matchId;

    private LocalDateTime timestamp = LocalDateTime.now();

}
