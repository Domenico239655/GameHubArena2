package com.gamehub.arena.dao;

import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.model.TournamentPlayerId;
import com.gamehub.arena.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TournamentPlayerIdRepository extends JpaRepository<TournamentPlayerId, Long> {
    Optional<TournamentPlayerId> findByTournamentAndUser(Tournament tournament, User user);
}