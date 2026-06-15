package com.gamehub.arena.controller;


import com.gamehub.arena.dto.NotificationCreateDTO;
import com.gamehub.arena.dto.NotificationResponseDTO;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import com.gamehub.arena.dao.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;
    private final UserRepository userRepository;

    public NotificationController(NotificationService service, UserRepository userRepository){
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping
    public NotificationResponseDTO send(@RequestBody NotificationCreateDTO dto, Authentication authentication){
        if (authentication != null) {
            dto.setSenderUsername(authentication.getName());
        }
        return service.send(dto);
    }

    @GetMapping("/my-messages")
    public List<NotificationResponseDTO> getMyNotifications(Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utente non trovato"));
        return service.getUserNotifications(user.getId());
    }

    @GetMapping("/user/{userId}")
    public List<NotificationResponseDTO> getUserNotifications(@PathVariable Long userId){
        return service.getUserNotifications(userId);
    }

    @PostMapping("/{id}/read")
    public NotificationResponseDTO markAsRead(@PathVariable Long id){
        return service.markAsRead(id);
    }
}
