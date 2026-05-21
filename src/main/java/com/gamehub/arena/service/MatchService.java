package com.gamehub.arena.service;

import com.gamehub.arena.model.Match;

import java.util.List;

public interface MatchService {
    Match create(Match m);
    List<Match> getAll();
}
