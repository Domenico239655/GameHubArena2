package com.gamehub.arena.dto;

import lombok.Data;

import java.time.LocalDateTime;

// TIPS: Pattern DTO (Data Transfer Object). Questa classe serve solo per "trasportare" dati
// dal frontend al backend senza esporre direttamente l'entità del database (Tournament.java).
// È una best-practice architetturale usata per disaccoppiare il database dal client.
@Data
public class TournamentCreateDTO {
    private String title;
    private Long gameId;
    private LocalDateTime startDate;
    private String description;
    private Integer maxParticipants;
}
