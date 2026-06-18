package com.gamehub.arena.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TournamentCreateDTO {
    private String title;
    private Long gameId;
    private LocalDate startDate;
    private String description;
    private Integer maxParticipants;
}
