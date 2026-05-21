package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.GameRepository;
import com.gamehub.arena.model.Game;
import com.gamehub.arena.service.GameService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository repo;
    public GameServiceImpl(GameRepository repo){
        this.repo = repo;
    }

    @Override
    public Game create(Game game){
        return repo.save(game);
    }

    @Override
    public List<Game> getAll() {
        return repo.findAll();
    }

    @Override
    public Game getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Gioco non trovato"));
    }
}
