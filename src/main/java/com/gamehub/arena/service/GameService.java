package com.gamehub.arena.service;

import com.gamehub.arena.dto.GameCreateDTO;
import com.gamehub.arena.dto.GameResponseDTO;
import com.gamehub.arena.model.Game;

import java.util.List;
import java.util.Optional;

public interface GameService {
    GameResponseDTO create(GameCreateDTO dto);
    List<GameResponseDTO> getAll();
    GameResponseDTO getById(Long id);
    GameResponseDTO toDTO(Game game);
    Game fromDTO(GameCreateDTO dto);
    Optional<Game> findEntityById(Long id);
    void delete(Long id);

}
