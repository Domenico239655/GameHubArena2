package com.gamehub.arena.service;

import com.gamehub.arena.model.Game;

import java.util.List;

public interface GameService {
    Game create(Game game);
    List<Game> getAll();
    Game getById(Long id);
}
