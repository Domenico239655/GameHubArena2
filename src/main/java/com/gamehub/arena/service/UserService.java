package com.gamehub.arena.service;

import com.gamehub.arena.dto.RegisterRequest;
import com.gamehub.arena.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserService {
    User register(RegisterRequest request);
    Optional<User> findByUsername(String username);
}
