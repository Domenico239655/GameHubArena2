package com.gamehub.arena.dao;

import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.model.TournamentRating;
import com.gamehub.arena.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TournamentRatingRepository extends JpaRepository<TournamentRating, Long> {
    Optional<TournamentRating> findByTournamentAndUser(Tournament tournament, User user);

    @Query("SELECT AVG(r.score) FROM TournamentRating r WHERE r.tournament = :tournament")
    Double getAverageScoreByTournament(@Param("tournament") Tournament tournament);

}
