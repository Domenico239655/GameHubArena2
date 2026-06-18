package com.gamehub.arena.dao;

import com.gamehub.arena.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByTitle(String title);
    Optional<Game> findByTitle(String title);
}
