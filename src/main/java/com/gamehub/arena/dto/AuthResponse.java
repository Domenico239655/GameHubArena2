package com.gamehub.arena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}
