package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class MatchCreateDTO {

    private Long tournamentId;
    private Long team1Id;
    private Long team2Id;
}
