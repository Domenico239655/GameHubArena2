package com.gamehub.arena.controller;


import com.gamehub.arena.dto.TournamentCreateDTO;
import com.gamehub.arena.dto.TournamentResponseDTO;
import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.service.TournamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private final TournamentService service;
    private final TournamentService tournamentService;

    public TournamentController(TournamentService service, TournamentService tournamentService){
        this.service = service;
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public TournamentResponseDTO create(@RequestBody TournamentCreateDTO dto){
        return service.create(dto);

    }

    @GetMapping
    public List<TournamentResponseDTO> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TournamentResponseDTO getById(@PathVariable Long id){

        return service.getById(id);
    }

    @PostMapping("/{tournamentId}/join/{userId}")
    public TournamentResponseDTO join(@PathVariable Long tournamentId, @PathVariable Long userId){
        return service.join(tournamentId, userId);
    }

    @PostMapping("/{tournamentId}/generate-bracket")
    public void generateBracket(@PathVariable Long tournamentId){
        tournamentService.generateBracket(tournamentId);
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateTournament(@PathVariable Long id, @RequestParam Long userId, @RequestParam int score) {
        try {
            tournamentService.addRating(id, userId, score);
            return ResponseEntity.ok(java.util.Map.of("message", "Rating salvato con successo!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
