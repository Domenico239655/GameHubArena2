package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.GameRepository;
import com.gamehub.arena.dto.GameCreateDTO;
import com.gamehub.arena.dto.GameExternalDTO;
import com.gamehub.arena.dto.GameResponseDTO;
import com.gamehub.arena.model.Game;
import com.gamehub.arena.service.GameService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository repo;
    private final RawgService rawgService;


    public GameServiceImpl(GameRepository repo, RawgService rawgService){
        this.repo = repo;
        this.rawgService = rawgService;
    }


    public Game createEntity(GameCreateDTO dto, String title){
        List<GameExternalDTO> results = rawgService.searchGames(title);
        GameExternalDTO rawg = results.isEmpty() ? null : results.get(0);

        Game game = fromDTO(dto);
        game.setTitle(title);

        if(rawg != null){
            game.setCoverUrl(rawg.getBackgroundImage());
            game.setRating(rawg.getRating());
            game.setGenere(dto.getGenere());
        }
        return repo.save(game);
    }

    @Override
    public GameResponseDTO create(GameCreateDTO dto){
        Game game = fromDTO(dto);
        repo.save(game);
        return toDTO(game);
    }

    @Override
    public List<GameResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public GameResponseDTO getById(Long id) {
        Game game = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Gioco non trovato"));
        return toDTO(game);
    }

    @Override
    public GameResponseDTO toDTO(Game game) {
        GameResponseDTO dto = new GameResponseDTO();
        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setGenere(game.getGenere());
        dto.setCoverUrl(game.getCoverUrl());
        dto.setRating(game.getRating());
        return dto;
    }

    @Override
    public Game fromDTO(GameCreateDTO dto) {
        Game game = new Game();
        game.setTitle(dto.getTitle());
        game.setGenere(dto.getGenere());
        return game;
    }

    @Override
    public Optional<Game> findEntityById(Long id) {
        return repo.findById(id);
    }
}
