package com.gamehub.arena.controller;

import com.gamehub.arena.dto.MatchCreateDTO;
import com.gamehub.arena.dto.MatchResponseDTO;
import com.gamehub.arena.dto.MatchResultDTO;
import com.gamehub.arena.model.Match;
import com.gamehub.arena.service.MatchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService service;

    public MatchController(MatchService service){
        this.service = service;
    }

    @PostMapping
    public MatchResponseDTO create(@RequestBody MatchCreateDTO dto){
        return service.create(dto);
    }

    @GetMapping
    public List<MatchResponseDTO> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MatchResponseDTO getById(@PathVariable Long id){
        return service.getById(id);
    }

    @PostMapping("/{matchId}/winner/{teamId}")
    public MatchResponseDTO setWinner(@PathVariable Long matchId, @PathVariable Long teamId){
        return service.setWinner(matchId, teamId);
    }

    @PostMapping("/{matchId}/result")
    public Match reportResult(@PathVariable Long matchId, @RequestBody MatchResultDTO dto){
        return service.reportResult(matchId, dto.getScorePlayer1(), dto.getScorePlayer2());
    }

    @GetMapping("/tournament/{tournamentId}")
    public List<Match> getMatchesByTournament(@PathVariable Long tournamentId){
        return service.getMatchesByTournament(tournamentId);
    }
}
