package com.gamehub.arena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@AllArgsConstructor

public class AuthResponse {
    private String token;
}
