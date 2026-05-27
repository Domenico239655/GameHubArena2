package com.gamehub.arena.service;

import com.gamehub.arena.dto.RegisterRequest;
import com.gamehub.arena.dto.UserCreateDTO;
import com.gamehub.arena.dto.UserLoginDTO;
import com.gamehub.arena.dto.UserResponseDTO;
import com.gamehub.arena.model.User;
import java.util.Optional;

public interface UserService {
    UserResponseDTO register(UserCreateDTO dto);
    UserResponseDTO login(UserLoginDTO dto);
    Optional<User> findByUsername(String username);
    UserResponseDTO toDTO(User user);
}
