package com.gamehub.arena.dto;

import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class LoginRequest {
    private String username;
    private String password;
}
