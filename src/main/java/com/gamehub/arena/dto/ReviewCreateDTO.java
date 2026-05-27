package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class ReviewCreateDTO {
    private int rating;
    private String comment;
    private Long userId;
    private Long gameId;
    private Long tournamentId;

}
