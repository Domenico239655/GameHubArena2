package com.gamehub.arena.controller;


import com.gamehub.arena.dto.TournamentCreateDTO;
import com.gamehub.arena.dto.TournamentResponseDTO;
import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.service.TournamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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
    public TournamentResponseDTO create(@RequestBody TournamentCreateDTO dto, Authentication authentication){
        String username = authentication.getName();
        return service.create(dto, username);
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

    @PostMapping("/{tournamentId}/player-id")
    public ResponseEntity<?> saveGameId(@PathVariable Long tournamentId, @RequestParam Long userId, @RequestParam String gameId) {
        try {
            tournamentService.savePlayerGameId(tournamentId, userId, gameId);
            return ResponseEntity.ok(java.util.Map.of("message", "Game ID salvato!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/{tournamentId}/player-id/{userId}")
    public ResponseEntity<?> getGameId(@PathVariable Long tournamentId, @PathVariable Long userId) {
        String gameId = tournamentService.getPlayerGameId(tournamentId, userId);

        
        return ResponseEntity.ok(java.util.Map.of("gameId", gameId != null ? gameId : ""));
    }

    @GetMapping("/{tournamentId}/my-match/{userId}")
    public ResponseEntity<?> getMyMatch(@PathVariable Long tournamentId, @PathVariable Long userId) {
        return ResponseEntity.ok(tournamentService.getMyMatch(tournamentId, userId));
    }

    @PostMapping("/match/{matchId}/report")
    public ResponseEntity<?> reportMatchResult(
            @PathVariable Long matchId,
            @RequestParam Long userId,
            @RequestParam boolean isWinner) {
        try {
            tournamentService.reportMatchResult(matchId, userId, isWinner);
            return ResponseEntity.ok(java.util.Map.of("message", "Risultato salvato con successo"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/potm")
    public ResponseEntity<?> getPlayerOfTheMonth(){return ResponseEntity.ok(tournamentService.getPlayerOfTheMonth());}

    @PostMapping("/match/{matchId}/upload-screenshot")
    public ResponseEntity<?> uploadScreenshot(
            @PathVariable Long matchId,
            @RequestParam Long userId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            String url = tournamentService.saveScreenshot(matchId, userId, file);
            return ResponseEntity.ok(java.util.Map.of("message", "Screenshot salvato", "url", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> getFile(@PathVariable String filename) {
        try {
            java.nio.file.Path file = java.nio.file.Paths.get(System.getProperty("user.dir"), "uploads").resolve(filename).normalize();
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = java.nio.file.Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
