package com.gamehub.arena.dto;

import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String ruolo;
}
