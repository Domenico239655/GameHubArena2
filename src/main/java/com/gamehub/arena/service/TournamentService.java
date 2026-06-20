package com.gamehub.arena.service;

import com.gamehub.arena.dto.TournamentCreateDTO;
import com.gamehub.arena.dto.TournamentResponseDTO;
import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.model.User;

import java.util.List;
import java.util.Optional;

public interface TournamentService {
    TournamentResponseDTO create(TournamentCreateDTO dto, String username);
    TournamentResponseDTO toDTO(Tournament t);
    Tournament fromDTO(TournamentCreateDTO dto);
    Optional<Tournament> findEntityById(Long id);
    List<TournamentResponseDTO> getAll();
    TournamentResponseDTO getById(Long id);
    TournamentResponseDTO join(Long tournamentId, Long userId);
    List<User> getParticipants(Long tournamentId);
    void generateBracket(Long tournamentId);

    void savePlayerGameId(Long tournamentId, Long userId, String gameId);
    String getPlayerGameId(Long tournamentId, Long userId);

    void reportMatchResult(Long matchId, Long userId, boolean isWinner);

    java.util.Map<String, Object> getMyMatch(Long tournamentId, Long userId);

    void addRating(Long tournamentId, Long userId, int score);

    java.util.Map<String, Object> getPlayerOfTheMonth();
}
