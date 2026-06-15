package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class GameResponseDTO {

    private Long id;
    private String title;
    private String genere;
    private String coverUrl;
    private double rating;
    private String rawgId;
    private String description;

}
