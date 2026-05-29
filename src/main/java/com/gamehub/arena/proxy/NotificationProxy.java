package com.gamehub.arena.proxy;

import com.gamehub.arena.dto.NotificationCreateDTO;
import com.gamehub.arena.dto.NotificationDTO;
import com.gamehub.arena.dto.NotificationResponseDTO;
import com.gamehub.arena.model.Notification;
import com.gamehub.arena.service.NotificationService;
import com.gamehub.arena.serviceImpl.NotificationServiceImpl;
import com.gamehub.arena.websocket.NotificationWebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
public class NotificationProxy implements NotificationService {

    @Autowired
    private NotificationWebSocketController wsController;
    private final NotificationServiceImpl real;

    public NotificationProxy(NotificationServiceImpl real){
        this.real = real;
    }

    @Override
    public NotificationResponseDTO send(NotificationCreateDTO dto){
        if(dto.getMessage() == null || dto.getMessage().isBlank()){
            throw new RuntimeException("Messaggio vuoto non consentito");
        }

        NotificationResponseDTO saved = real.send(dto);

        NotificationDTO wsDto = new NotificationDTO();
        wsDto.setType(saved.getType());
        wsDto.setMessage(saved.getMessage());
        wsDto.setUserId(saved.getUserId());
        wsDto.setRelatedId(saved.getRelatedId());
        
        wsController.sendNotification(saved.getUserId(), wsDto);
        
        return saved;
    }

    @Override
    public List<NotificationResponseDTO> getUserNotifications(Long userId){
        return real.getUserNotifications(userId);
    }

    @Override
    public NotificationResponseDTO markAsRead(Long id){
        return real.markAsRead(id);
    }

    public void sendNotification(Notification notification){
        if(notification.getMessage().length() > 300){
            throw new RuntimeException("Messaggio troppo lungo");
        }

        real.sendNotification(notification);
        NotificationDTO dto = new NotificationDTO();
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setUserId(notification.getId());
        dto.setRelatedId(notification.getRelatedId());

        wsController.sendNotification(notification.getUser().getId(), dto);
    }

}
