package com.gamehub.arena.dto;

import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
