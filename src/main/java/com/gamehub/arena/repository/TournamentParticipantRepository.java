package com.gamehub.arena.repository;

import com.gamehub.arena.model.TournamentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {
}
