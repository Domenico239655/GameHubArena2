package com.gamehub.arena.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentCreateDTO {
    private String title;
    private Long gameId;
    private LocalDateTime startDate;
    private String description;
    private Integer maxParticipants;
}
