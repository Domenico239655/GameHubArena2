package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.MatchRepository;
import com.gamehub.arena.model.Match;
import com.gamehub.arena.service.MatchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository repo;

    public MatchServiceImpl(MatchRepository repo){
        this.repo = repo;
    }

    @Override
    public Match create(Match m) {
        return repo.save(m);
    }

    @Override
    public List<Match> getAll() {
        return repo.findAll();
    }
}
