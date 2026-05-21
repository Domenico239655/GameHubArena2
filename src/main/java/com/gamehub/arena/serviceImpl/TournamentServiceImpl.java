package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.TournamentRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.TournamentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository repo;
    private final UserRepository userRepo;

    public TournamentServiceImpl(TournamentRepository repo, UserRepository userRepo){
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    public Tournament create(Tournament t) {
        return repo.save(t);
    }

    @Override
    public List<Tournament> getAll() {
        return repo.findAll();
    }

    @Override
    public Tournament getById(Long id) {
        return repo.findById(id)
                .orElseThrow(()->new RuntimeException("Torneo non trovato"));
    }

    @Override
    public Tournament join(Long tournamentId, Long userId) {
        Tournament t = getById(tournamentId);
        User u = userRepo.findById(userId)
                .orElseThrow(()-> new RuntimeException("Utente non trovato"));
        t.getPartecipants().add(u);
        return repo.save(t);
    }
}
