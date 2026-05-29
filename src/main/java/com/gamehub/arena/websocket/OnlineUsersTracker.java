package com.gamehub.arena.websocket;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUsersTracker {
    private final Map<Long, Set<String>> onlineUsers = new ConcurrentHashMap<>();

    public void userJoined(Long tournamentId, String username){
        onlineUsers.computeIfAbsent(tournamentId, k -> ConcurrentHashMap.newKeySet())
                .add(username);
    }

    public void userLeft(Long tournamentId, String username){
        if(onlineUsers.containsKey(tournamentId)){
            onlineUsers.get(tournamentId).remove(username);
        }
    }

    public Set<String> getOnlineUsers(Long tournamentId){
        return onlineUsers.getOrDefault(tournamentId, Collections.emptySet());
    }
}
