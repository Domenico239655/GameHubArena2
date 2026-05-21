package com.gamehub.arena.service;

import com.gamehub.arena.model.Team;

public interface TeamService {
    Team create(Team t);
    Team addMember(Long teamId, Long userId);
}
