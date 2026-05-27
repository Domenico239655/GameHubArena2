package com.gamehub.arena.service;

import com.gamehub.arena.dto.MatchCreateDTO;
import com.gamehub.arena.dto.MatchResponseDTO;
import com.gamehub.arena.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchService {
    MatchResponseDTO create(MatchCreateDTO dto);
    List<MatchResponseDTO> getAll();
    MatchResponseDTO getById(Long id);
    MatchResponseDTO setWinner(Long matchId, Long winnerTeamId);
    MatchResponseDTO toDTO(Match match);
    Match fromDTO(MatchCreateDTO dto);
    Optional<Match> findEntityById(Long id);
    Match reportResult(Long matchId, int score1, int score2);
    List<Match> getMatchesByTournament(Long tournamentId);

}
