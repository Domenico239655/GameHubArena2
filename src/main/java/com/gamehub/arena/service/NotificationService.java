package com.gamehub.arena.service;

import com.gamehub.arena.dto.NotificationCreateDTO;
import com.gamehub.arena.dto.NotificationResponseDTO;
import com.gamehub.arena.model.Notification;
import com.gamehub.arena.model.User;

import java.util.List;

public interface NotificationService {
    NotificationResponseDTO send(NotificationCreateDTO dto);
    List<NotificationResponseDTO> getUserNotifications(Long userId);
    NotificationResponseDTO markAsRead(Long id);
    void sendNotification(Notification notification);
}
