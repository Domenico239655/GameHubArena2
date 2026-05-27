package com.gamehub.arena.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TournamentResponseDTO {
    private Long id;
    private String title;
    private String game;
    private LocalDate date;
    private int participantsCount;



}
