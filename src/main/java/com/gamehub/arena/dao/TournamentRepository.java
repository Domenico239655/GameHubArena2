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

    @org.springframework.data.jpa.repository.Query("SELECT t.winner.username, COUNT(t) as wins FROM Tournament t WHERE t.status = 'CONCLUSO' AND FUNCTION('MONTH', t.endDate) = FUNCTION('MONTH', CURRENT_DATE) AND FUNCTION('YEAR', t.endDate) = FUNCTION('YEAR', CURRENT_DATE) GROUP BY t.winner.username ORDER BY wins DESC")
    List<Object[]> findPlayerofTheMonth();
    @org.springframework.data.jpa.repository.Query("SELECT t.status FROM Tournament t WHERE t.id = :id")
    String getStatusById(@org.springframework.data.repository.query.Param("id") Long id);
}
