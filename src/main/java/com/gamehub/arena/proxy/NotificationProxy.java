package com.gamehub.arena.proxy;

import com.gamehub.arena.model.User;
import com.gamehub.arena.service.NotificationService;
import com.gamehub.arena.serviceImpl.NotificationServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NotificationProxy implements NotificationService {

    private final NotificationServiceImpl realService;

    public NotificationProxy(NotificationServiceImpl realService){
        this.realService = realService;
    }

    @Override
    public void send(User user, String message) {
        if(message.contains("spam")){
            System.out.println("NOTIFICA BLOCCATA DAL PROXY");
            return;
        }
        realService.send(user, message);
    }
}
