package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class MatchResponseDTO {
    private Long id;
    private Long tournamentId;
    private String team1Name;
    private String team2Name;
    private String winnerName;
    private String status;
}
