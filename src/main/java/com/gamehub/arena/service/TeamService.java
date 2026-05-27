package com.gamehub.arena.service;

import com.gamehub.arena.dto.TeamCreateDTO;
import com.gamehub.arena.dto.TeamResponseDTO;
import com.gamehub.arena.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    TeamResponseDTO create(TeamCreateDTO dto);
    List<TeamResponseDTO> getAll();
    TeamResponseDTO getById(Long id);
    TeamResponseDTO addMember(Long teamId, Long userId);
    TeamResponseDTO toDTO(Team team);
    Team fromDTO(TeamCreateDTO dto);
    Optional<Team> findEntityById(Long id);

}
