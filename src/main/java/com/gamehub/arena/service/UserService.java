package com.gamehub.arena.service;

import com.gamehub.arena.model.User;

import java.util.Optional;

public interface UserService {
    User register(User user);
    Optional<User> findByUsername(String username);
}
