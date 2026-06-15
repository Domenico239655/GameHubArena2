package com.gamehub.arena.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    @JsonProperty("role")
    private String role;
    private int rank;
}
