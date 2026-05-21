package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.model.User;
import com.gamehub.arena.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void send(User user, String message) {
        System.out.println("NOTIFICA INVIATA A " + user.getUsername());
    }
}
