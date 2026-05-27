package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class ReviewResponseDTO {

    private Long id;
    private int rating;
    private String comment;
    private String username;
    private String gameName;
    private String tournamentTitle;


}
