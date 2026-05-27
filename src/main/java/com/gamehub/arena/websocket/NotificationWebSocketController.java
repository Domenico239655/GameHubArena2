package com.gamehub.arena.websocket;

import com.gamehub.arena.dto.NotificationDTO;
import com.gamehub.arena.dto.NotificationResponseDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketController(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(Long userId, NotificationDTO notificationDto){
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notificationDto);
    }
}
