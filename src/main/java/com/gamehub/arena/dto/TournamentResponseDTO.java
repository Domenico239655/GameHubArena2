package com.gamehub.arena.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TournamentResponseDTO {
    private Long id;
    private String title;
    private GameResponseDTO game;
    private boolean registrationOpen;
    private int participantsCount;
    private String gameImageUrl;
    private String description;
    private List<TeamResponseDTO> teams;
    private Double rating;


}
