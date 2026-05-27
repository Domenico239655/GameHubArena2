package com.gamehub.arena.service;

import com.gamehub.arena.model.Match;

public interface EloService {
    void updateEloAfterMatch(Match match);
}
