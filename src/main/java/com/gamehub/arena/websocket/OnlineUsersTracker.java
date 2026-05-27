package com.gamehub.arena.websocket;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OnlineUsersTracker {
    private final Map<Long, Set<String>> onlineUsers = new HashMap<>();
    public void userJoined(Long tournametId, String username){
        onlineUsers.computeIfAbsent(tournametId, k -> new HashSet<>())
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
