package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class TeamResponseDTO {

    private Long id;
    private String name;
    private String ownerUsername;
    private int membersCount;
}
