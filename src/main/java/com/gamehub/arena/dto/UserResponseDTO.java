package com.gamehub.arena.dto;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private int rank;
}
