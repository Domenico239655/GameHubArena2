package com.gamehub.arena.dao;

import com.gamehub.arena.model.Tournament;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Override
    @EntityGraph(attributePaths = {"participants", "game"})
    List<Tournament> findAll();
}
