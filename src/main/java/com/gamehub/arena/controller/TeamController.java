package com.gamehub.arena.controller;

import com.gamehub.arena.dto.TeamCreateDTO;
import com.gamehub.arena.dto.TeamResponseDTO;
import com.gamehub.arena.model.Team;
import com.gamehub.arena.service.TeamService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService service;

    public TeamController(TeamService service){
        this.service = service;
    }

    @PostMapping
    public TeamResponseDTO create(@RequestBody TeamCreateDTO t){
        return service.create(t);
    }

    @PostMapping("/{teamId}/add/{userId}")
    public TeamResponseDTO addMember(@PathVariable Long teamId, @PathVariable Long userId){
        return service.addMember(teamId, userId);
    }
}
