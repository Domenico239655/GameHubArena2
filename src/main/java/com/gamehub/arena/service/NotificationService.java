package com.gamehub.arena.service;

import com.gamehub.arena.model.User;

public interface NotificationService {
    void send(User user, String message);
}
