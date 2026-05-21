package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.TeamRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.model.Team;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.TeamService;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository repo;
    private final UserRepository userRepo;

    public TeamServiceImpl(TeamRepository repo, UserRepository userRepo){
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    public Team create(Team t) {
        return repo.save(t);
    }

    @Override
    public Team addMember(Long teamId, Long userId) {
        Team team = repo.findById(teamId)
                .orElseThrow(()->new RuntimeException("Team non trovato"));
        User user = userRepo.findById(userId)
                .orElseThrow(()-> new RuntimeException("Utente non trovato"));
        team.getMenbers().add(user);
        return repo.save(team);
    }
}
