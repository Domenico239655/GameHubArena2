package com.gamehub.arena.dao;

import com.gamehub.arena.model.User;

import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> findByUsername(String username);
}
