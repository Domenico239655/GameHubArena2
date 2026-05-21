package com.gamehub.arena.service;

import com.gamehub.arena.model.Tournament;

import java.util.List;

public interface TournamentService {
    Tournament create(Tournament t);
    List<Tournament> getAll();
    Tournament getById(Long id);
    Tournament join(Long tournamentId, Long userId);
}
