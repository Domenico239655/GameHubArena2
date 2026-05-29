package com.gamehub.arena.controller;

import com.gamehub.arena.dto.GameCreateDTO;
import com.gamehub.arena.dto.GameExternalDTO;
import com.gamehub.arena.dto.GameResponseDTO;
import com.gamehub.arena.model.Game;
import com.gamehub.arena.service.GameService;
import com.gamehub.arena.serviceImpl.RawgService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:4200")
public class GameController {
    private final GameService service;
    private final RawgService rawgService;

    public GameController(GameService service, RawgService rawgService){
        this.service = service;
        this.rawgService = rawgService;
    }

    @PostMapping
    public GameResponseDTO create(@RequestBody GameCreateDTO game){return service.create(game);
    }

    @GetMapping
    public List<GameResponseDTO> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public GameResponseDTO getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping("/search")
    public List<GameExternalDTO> search(@RequestParam String query){
        return rawgService.searchGames(query);
    }
}
