package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.MatchRepository;
import com.gamehub.arena.dao.TeamRepository;
import com.gamehub.arena.dao.TournamentRepository;
import com.gamehub.arena.dto.MatchCreateDTO;
import com.gamehub.arena.dto.MatchResponseDTO;
import com.gamehub.arena.dto.NotificationCreateDTO;
import com.gamehub.arena.model.*;
import com.gamehub.arena.service.EloService;
import com.gamehub.arena.service.MatchService;
import com.gamehub.arena.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository repo;
    private final TournamentRepository tournamentRepo;
    private final TeamRepository teamRepo;
    private final NotificationService notificationService;
    private final EloService eloService;


    public MatchServiceImpl(MatchRepository repo, TournamentRepository tournamentRepo, TeamRepository teamRepo, NotificationService notificationService, EloService eloService){
        this.repo = repo;
        this.tournamentRepo = tournamentRepo;
        this.teamRepo = teamRepo;
        this.notificationService = notificationService;
        this.eloService = eloService;
    }

    @Override
    public MatchResponseDTO create(MatchCreateDTO dto) {
        Match match = fromDTO(dto);
        repo.save(match);
        return toDTO(match);
    }

    @Override
    public List<MatchResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public MatchResponseDTO getById(Long id) {
        Match match = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Match non trovato|"));
        return toDTO(match);
    }

    @Override
    public MatchResponseDTO setWinner(Long matchId, Long winnerTeamId) {
        Match match = repo.findById(matchId)
                .orElseThrow(()->new RuntimeException("Match non trovato|"));
        Team winner = teamRepo.findById(winnerTeamId)
                .orElseThrow(()->new RuntimeException("Team vincitore non trovato!"));
        match.setWinner(winner);
        match.setStatus("FINISHED");
        repo.save(match);

        Tournament tournament = match.getTournament();
        Team team1 = match.getTeam1();
        Team team2 = match.getTeam2();
        Team loser = team1.getId().equals(winner.getId()) ? team2 : team1;

        for(User u : winner.getMembers()){
            NotificationCreateDTO dto = new NotificationCreateDTO();
            dto.setUserId(u.getId());
            dto.setMessage("Hai vinto il match nel tornero " + tournament.getTitle()
            +" con il team " + winner.getName() + " .");
            notificationService.send(dto);
        }

        for(User u : loser.getMembers()){
            NotificationCreateDTO dto = new NotificationCreateDTO();
            dto.setUserId(u.getId());
            dto.setMessage("Hai perso il match nel torneo " + tournament.getTitle()
            +" contro il team " + winner.getName() + " .");
            notificationService.send(dto);
        }

        User organizer = tournament.getOrganizer();
        if(organizer != null){
            NotificationCreateDTO dto = new NotificationCreateDTO();
            dto.setUserId(organizer.getId());
            dto.setMessage("Il match tra " + team1.getName() + " e " + team2.getName() + " è terminato. Vincitore: " + winner.getName() + " .");
            notificationService.send(dto);
        }
        return toDTO(match);
    }

    @Override
    public MatchResponseDTO toDTO(Match match) {
        MatchResponseDTO dto = new MatchResponseDTO();
        dto.setId(match.getId());
        dto.setTournamentId(match.getTournament().getId());
        dto.setTeam1Name(match.getTeam1().getName());
        dto.setTeam2Name(match.getTeam2().getName());
        dto.setWinnerName(match.getWinner() != null ? match.getWinner().getName() : null);
        dto.setStatus(match.getStatus());
        return dto;
    }

    @Override
    public Match fromDTO(MatchCreateDTO dto) {
        Match match = new Match();
        Tournament tournament = tournamentRepo.findById(dto.getTournamentId())
                .orElseThrow(()->new RuntimeException("Torneo non trovato!"));
        Team team1 = teamRepo.findById(dto.getTeam1Id())
                .orElseThrow(() -> new RuntimeException("Team A non trovato!"));
        Team team2 = teamRepo.findById(dto.getTeam2Id())
                .orElseThrow(() -> new RuntimeException("Team B non trovato!"));

        match.setTournament(tournament);
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setStatus("SCHEDULED");

        return match;
    }

    @Override
    public Optional<Match> findEntityById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Match reportResult(Long matchId, int score1, int score2) {
        Match m = repo.findById(matchId)
                .orElseThrow(()->new RuntimeException("Match non trovato!"));
        m.setScorePlayer1(score1);
        m.setScorePlayer2(score2);
        m.setStato(MatchStatus.FINISHED);

        repo.save(m);
        User winner = score1 > score2 ? m.getPlayer1() : m.getPlayer2();
        advanceWinner(m, winner);

        eloService.updateEloAfterMatch(m);
        return m;
    }

    private void advanceWinner(Match match, User winner){
        int nextRound = match.getRoundNumber() + 1;

        int matchIndex = match.getId().intValue();
        int nextMatchIndex = matchIndex/ 2;

        List<Match> nextRoundMatches = repo.findByTournamentIdOrderByRoundNumberAsc(match.getTournament().getId())
                .stream()
                .filter(m -> m.getRoundNumber() == nextRound)
                .toList();

        Match nextMatch;
        if(nextMatchIndex >= nextRoundMatches.size()){
            nextMatch = new Match();
            nextMatch.setTournament(match.getTournament());
            nextMatch.setRoundNumber(nextRound);
            nextMatch.setStato(MatchStatus.PENDING);
        }else{
            nextMatch = nextRoundMatches.get(nextMatchIndex);
        }

        if(nextMatch.getPlayer1() == null){
            nextMatch.setPlayer1(winner);
        }else{
            nextMatch.setPlayer2(winner);
        }
        repo.save(nextMatch);
    }

    @Override
    public List<Match> getMatchesByTournament(Long tournamentId) {
        return repo.findByTournamentIdOrderByRoundNumberAsc(tournamentId);
    }
}
