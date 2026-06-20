package com.gamehub.arena.service;

import com.gamehub.arena.dto.*;
import com.gamehub.arena.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO register(UserCreateDTO dto);
    UserResponseDTO login(UserLoginDTO dto);
    Optional<User> findByUsername(String username);
    UserResponseDTO toDTO(User user);

    List<GameResponseDTO> getLibrary(String username);
    void addGameToLibrary(String username, Long gameId);
    void removeGameFromLibrary(String username, Long gameId);
}
