package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.TeamRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.dto.TeamCreateDTO;
import com.gamehub.arena.dto.TeamResponseDTO;
import com.gamehub.arena.model.Team;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.TeamService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository repo;
    private final UserRepository userRepo;

    public TeamServiceImpl(TeamRepository repo, UserRepository userRepo){
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    public TeamResponseDTO create(TeamCreateDTO dto) {
        Team team = fromDTO(dto);
        repo.save(team);
        return toDTO(team);
    }

    @Override
    public List<TeamResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public TeamResponseDTO getById(Long id) {
        Team team = repo.findById(id)
                .orElseThrow(()-> new RuntimeException("Team non trovato"));
        return toDTO(team);
    }

    @Override
    public TeamResponseDTO addMember(Long teamId, Long userId) {
        Team team = repo.findById(teamId)
                .orElseThrow(()->new RuntimeException("Team non trovato"));
        User user = userRepo.findById(userId)
                .orElseThrow(()-> new RuntimeException("Utente non trovato"));
        team.getMembers().add(user);
        repo.save(team);
        return toDTO(team);
    }

    @Override
    public TeamResponseDTO toDTO(Team team) {
        TeamResponseDTO dto = new TeamResponseDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setOwnerUsername(team.getOwner().getUsername());
        dto.setMembersCount(team.getMembers().size());
        return dto;
    }

    @Override
    public Team fromDTO(TeamCreateDTO dto){
        Team team = new Team();
        team.setName(dto.getName());

        User owner = userRepo.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner non trovato"));

        team.setOwner(owner);
        team.getMembers().add(owner); // il creatore entra automaticamente nel team

        return team;
    }

    @Override
    public Optional<Team> findEntityById(Long id){
        return repo.findById(id);
    }

}
