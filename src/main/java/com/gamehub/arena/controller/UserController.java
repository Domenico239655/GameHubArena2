package com.gamehub.arena.controller;

import com.gamehub.arena.dto.UserResponseDTO;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @GetMapping("/{username}")
    public UserResponseDTO getByUsername(@PathVariable String username){
        User user = service.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Utente non trovato!"));
        return service.toDTO(user);
    }
}
