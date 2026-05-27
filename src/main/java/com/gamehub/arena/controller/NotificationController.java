package com.gamehub.arena.controller;


import com.gamehub.arena.dto.NotificationCreateDTO;
import com.gamehub.arena.dto.NotificationResponseDTO;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service){
        this.service = service;
    }

    @PostMapping
    public NotificationResponseDTO send(@RequestBody NotificationCreateDTO dto){
        return service.send(dto);
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
