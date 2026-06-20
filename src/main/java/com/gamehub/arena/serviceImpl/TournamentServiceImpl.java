package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.*;
import com.gamehub.arena.dto.*;
import com.gamehub.arena.model.*;
import com.gamehub.arena.service.GameService;
import com.gamehub.arena.service.NotificationService;
import com.gamehub.arena.service.TournamentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository repo;
    private final UserRepository userRepo;
    private final GameRepository gameRepo;
    private final NotificationService notificationService;
    private final MatchRepository matchRepo;
    private final GameService gameService;
    private final TournamentRatingRepository ratingRepo;
    private final TournamentPlayerIdRepository playerIdRepo;

    public TournamentServiceImpl(TournamentRepository repo, UserRepository userRepo, GameRepository gameRepo, NotificationService notificationService, MatchRepository matchRepo, GameService gameService, TournamentRatingRepository ratingRepo, TournamentPlayerIdRepository playerIdRepo){
        this.repo = repo;
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.notificationService = notificationService;
        this.matchRepo = matchRepo;
        this.gameService = gameService;
        this.ratingRepo = ratingRepo;
        this.playerIdRepo = playerIdRepo;
    }

    @Override
    public TournamentResponseDTO create(TournamentCreateDTO dto, String username) {
        User organizer = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organizzatore non trovato!"));
        Tournament t = fromDTO(dto);
        t.setOrganizer(organizer); // <--- Associa l'utente loggato come organizzatore!
        repo.save(t);
        return toDTO(t);
    }

    @Override
    public TournamentResponseDTO getById(Long id) {
        Tournament t = repo.findById(id)
                .orElseThrow(()->new RuntimeException("Torneo non trovato"));

        if ("ISCRIZIONI_APERTE".equals(t.getStatus()) && t.getStartDate() != null) {

            // Controlla se l'orario attuale è MAGGIORE o UGUALE all'orario di inizio
            if (java.time.LocalDateTime.now().isAfter(t.getStartDate()) || java.time.LocalDateTime.now().isEqual(t.getStartDate())) {
                try {
                    // Genera gli accoppiamenti casuali
                    this.generateBracket(t.getId());
                    // Cambia lo stato per bloccare nuove iscrizioni
                    //t.setStatus("IN_CORSO");
                    //repo.save(t);
                } catch (Exception e) {
                    System.out.println("Nessun giocatore iscritto o errore: " + e.getMessage());
                }
            }
        }

        return toDTO(t);
    }

    @Override
    public List<TournamentResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }



    @Override
    public TournamentResponseDTO join(Long tournamentId, Long userId) {
        Tournament t = repo.findById(tournamentId)
                .orElseThrow(()->new RuntimeException("Torneo non trovato!"));

        User u = userRepo.findById(userId)
                .orElseThrow(()-> new RuntimeException("Utente non trovato"));

        if (!"ISCRIZIONI_APERTE".equals(t.getStatus())) {
            throw new RuntimeException("Le iscrizioni a questo torneo sono chiuse!");
        }

        if (t.getParticipants().contains(u)) {
            throw new RuntimeException("Sei già iscritto a questo torneo!");
        }

        if (t.getMaxParticipants() != null && t.getParticipants().size() >= t.getMaxParticipants()) {
            throw new RuntimeException("Il torneo ha raggiunto il numero massimo di partecipanti!");
        }

        t.getParticipants().add(u);
        repo.save(t);

        NotificationCreateDTO dtoUser = new NotificationCreateDTO();
        dtoUser.setUserId(userId);
        dtoUser.setMessage("Ti sei iscritto al torneo: " + t.getTitle());
        notificationService.send(dtoUser);

        if (t.getOrganizer() != null) {
            NotificationCreateDTO dtoOrg = new NotificationCreateDTO();
            dtoOrg.setUserId(t.getOrganizer().getId());
            dtoOrg.setMessage("Nuovo iscritto al tuo torneo: " + u.getUsername());
            notificationService.send(dtoOrg);
        }

        return toDTO(t);
    }

    @Override
    public TournamentResponseDTO toDTO(Tournament t) {
        TournamentResponseDTO dto = new TournamentResponseDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setGame(gameService.toDTO(t.getGame()));
        dto.setParticipantsCount(t.getParticipants().size());
        dto.setDescription(t.getDescription());
        if (t.getParticipants() != null) {
            dto.setParticipantsCount(t.getParticipants().size());

            // USIAMO IL NUOVO TeamResponseDTO
            List<TeamResponseDTO> mappedTeams = t.getParticipants().stream().map(user -> {
                TeamResponseDTO teamDto = new TeamResponseDTO();
                teamDto.setId(user.getId());
                teamDto.setName(user.getUsername());
                teamDto.setScore(0); // Score di default
                return teamDto;
            }).toList();

            dto.setTeams(mappedTeams);
        } else {
            dto.setParticipantsCount(0);
            dto.setTeams(new ArrayList<>());
        }
        if (t.getGame() != null) {
            dto.setGameImageUrl(t.getGame().getCoverUrl());
        }
        if (t.getStartDate() != null){
            dto.setStartDate(t.getStartDate());
            boolean isOpen = java.time.LocalDateTime.now().isBefore(t.getStartDate()) || java.time.LocalDateTime.now().isEqual(t.getStartDate());
            dto.setRegistrationOpen(isOpen);
        } else { dto.setRegistrationOpen(true);}

        dto.setRating(t.getRating() != null ? t.getRating() : 0.0);

        return dto;
    }

    @Override
    public Tournament fromDTO(TournamentCreateDTO dto){
        Tournament t = new Tournament();
        t.setTitle(dto.getTitle());
        t.setStartDate(dto.getStartDate());

        t.setDescription(dto.getDescription());
        t.setStatus("ISCRIZIONI_APERTE");

        t.setMaxParticipants(dto.getMaxParticipants());

        Game game = gameRepo.findById(dto.getGameId())
                .orElseThrow(() -> new RuntimeException("Game non trovato"));
        t.setGame(game);

        return t;
    }

    @Override
    public Optional<Tournament> findEntityById(Long id){
        return repo.findById(id);
    }

    @Override
    public List<User> getParticipants(Long tournamentId) {
        Tournament t = repo.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo non trovato!"));
        return t.getParticipants();
    }

    @Override
    public synchronized void generateBracket(Long tournamentId) {
        Tournament t = repo.findById(tournamentId)
                .orElseThrow(()-> new RuntimeException("Torneo non trovato!"));

        if (!"ISCRIZIONI_APERTE".equals(t.getStatus())) {
            return;
        }
        t.setStatus("IN_CORSO");
        repo.save(t);
        List<User> players = new ArrayList<>(t.getParticipants());

        java.util.Collections.shuffle(players);

        int size = players.size();
        if (size == 0) {
            throw new RuntimeException("Nessun partecipante iscritto al torneo!");
        }

        int nextPowerOfTwo = 1;
        while(nextPowerOfTwo < size) nextPowerOfTwo*=2;
        while(players.size() < nextPowerOfTwo){
            players.add(null);
        }
        for(int i = 0; i < players.size(); i += 2){
            Match m = new Match();
            m.setTournament(t);
            m.setPlayer1(players.get(i));
            m.setPlayer2(players.get(i + 1));
            m.setRoundNumber(1);
            m.setStato(MatchStatus.PENDING);

            matchRepo.save(m);
        }
    }

    @Override
    public void addRating(Long tournamentId, Long userId, int score){
        if(score < 1 || score > 5) throw new IllegalArgumentException("Voto non valido(1-5)");

        Tournament t = repo.findById(tournamentId).orElseThrow(() -> new RuntimeException("Torneo non trovato"));
        User u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Utente non trovato"));

        TournamentRating rating = ratingRepo.findByTournamentAndUser(t, u).orElse(new TournamentRating());
        rating.setTournament(t);
        rating.setUser(u);
        rating.setScore(score);
        ratingRepo.save(rating);

        Double average = ratingRepo.getAverageScoreByTournament(t);
        double roundedAvg = Math.round(average * 10.0) / 10.0;
        t.setRating(roundedAvg);
        repo.save(t);

    }

    // Metodo per salvare il Game ID
    @Override
    public void savePlayerGameId(Long tournamentId, Long userId, String gameId) {
        Tournament t = repo.findById(tournamentId).orElseThrow(() -> new RuntimeException("Torneo non trovato"));
        User u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Controlla se esiste già, così può essere inserito "una volta sola"
        if (playerIdRepo.findByTournamentAndUser(t, u).isPresent()) {
            throw new RuntimeException("Game ID già inserito per questo torneo!");
        }

        TournamentPlayerId tpi = new TournamentPlayerId();
        tpi.setTournament(t);
        tpi.setUser(u);
        tpi.setGameId(gameId);
        playerIdRepo.save(tpi);
    }

    // Metodo per recuperarlo (per nascondere il campo di testo se già inserito)
    @Override
    public String getPlayerGameId(Long tournamentId, Long userId) {
        Tournament t = repo.findById(tournamentId).orElseThrow();
        User u = userRepo.findById(userId).orElseThrow();

        return playerIdRepo.findByTournamentAndUser(t, u)
                .map(TournamentPlayerId::getGameId)
                .orElse(null);
    }

    @Override
    public java.util.Map<String, Object> getMyMatch(Long tournamentId, Long userId) {
        Tournament t = repo.findById(tournamentId).orElseThrow();
        // Grilletto Automatico
        if ("ISCRIZIONI_APERTE".equals(t.getStatus()) && t.getStartDate() != null) {
            if (java.time.LocalDateTime.now().isAfter(t.getStartDate()) || java.time.LocalDateTime.now().isEqual(t.getStartDate())) {
                try {
                    this.generateBracket(t.getId());
                    //t.setStatus("IN_CORSO");
                    //repo.save(t);
                } catch (Exception e) {}
            }
        }
        List<Match> matches = matchRepo.findByTournamentIdOrderByRoundNumberAsc(tournamentId);
        Match myMatch = null;
        User opponent = null;
        // Cerca il match attivo (non ci fermiamo solo a PENDING ora!)
        for (int i = matches.size() - 1; i >= 0; i--) {
            Match m = matches.get(i);
            if (m.getStato() == MatchStatus.PENDING || m.getStato() == MatchStatus.FINISHED || m.getStato() == MatchStatus.DISPUTED) {
                if (m.getPlayer1() != null && m.getPlayer1().getId().equals(userId)) {
                    myMatch = m; opponent = m.getPlayer2(); break;
                }
                if (m.getPlayer2() != null && m.getPlayer2().getId().equals(userId)) {
                    myMatch = m; opponent = m.getPlayer1(); break;
                }
            }
        }
        if (myMatch == null) {
            return java.util.Map.of("message", "Nessun match attivo trovato.");
        }
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("matchId", myMatch.getId());
        response.put("matchStatus", myMatch.getStato().name()); // PENDING, FINISHED o DISPUTED
        boolean hasReported = false;
        boolean isWinner = false;
        // Logica per sapere se IO ho votato e se IO ho vinto
        if (myMatch.getPlayer1() != null && myMatch.getPlayer1().getId().equals(userId)) {
            hasReported = (myMatch.getScorePlayer1() != null);
            if (myMatch.getStato() == MatchStatus.FINISHED) {
                isWinner = (myMatch.getScorePlayer1() != null && myMatch.getScorePlayer1() == 1);
            }
        } else if (myMatch.getPlayer2() != null && myMatch.getPlayer2().getId().equals(userId)) {
            hasReported = (myMatch.getScorePlayer2() != null);
            if (myMatch.getStato() == MatchStatus.FINISHED) {
                isWinner = (myMatch.getScorePlayer2() != null && myMatch.getScorePlayer2() == 1);
            }
        }

        response.put("hasReported", hasReported);
        response.put("isWinner", isWinner);

        if (opponent != null) {
            response.put("opponentName", opponent.getUsername());
            String opponentGameId = playerIdRepo.findByTournamentAndUser(t, opponent)
                    .map(TournamentPlayerId::getGameId).orElse("Non inserito");
            response.put("opponentGameId", opponentGameId);
        }
        return response;
    }

    @Override
    public void reportMatchResult(Long matchId, Long userId, boolean isWinner) {
        Match m = matchRepo.findById(matchId).orElseThrow(() -> new RuntimeException("Match non trovato"));

        int score = isWinner ? 1 : 0;

        // Salviamo la risposta del giocatore
        if (m.getPlayer1() != null && m.getPlayer1().getId().equals(userId)) {
            m.setScorePlayer1(score);
        } else if (m.getPlayer2() != null && m.getPlayer2().getId().equals(userId)) {
            m.setScorePlayer2(score);
        } else {
            throw new RuntimeException("Non fai parte di questo match!");
        }
        // Controllo intelligente: se entrambi hanno votato, valutiamo chi ha vinto
        if (m.getScorePlayer1() != null && m.getScorePlayer2() != null) {
            if (m.getScorePlayer1() == 1 && m.getScorePlayer2() == 0) {
                m.setStato(MatchStatus.FINISHED); // Player 1 vince pulito
            } else if (m.getScorePlayer2() == 1 && m.getScorePlayer1() == 0) {
                m.setStato(MatchStatus.FINISHED); // Player 2 vince pulito
            } else {
                // Risultati discordanti! Entrambi dicono di aver vinto!
                m.setStato(MatchStatus.DISPUTED);
            }
        }
        matchRepo.save(m);
        if (m.getStato() == MatchStatus.FINISHED) {
            checkAndGenerateNextRound(m.getTournament().getId(), m.getRoundNumber());
        }
    }

    private void checkAndGenerateNextRound(Long tournamentId, int currentRound) {
        List<Match> allMatches = matchRepo.findByTournamentIdOrderByRoundNumberAsc(tournamentId);
        // Prendi solo le partite di questo round
        List<Match> currentRoundMatches = allMatches.stream()
                .filter(m -> m.getRoundNumber() == currentRound)
                .toList();
        // Controlla se sono TUTTE FINITE
        boolean allFinished = currentRoundMatches.stream()
                .allMatch(m -> m.getStato() == MatchStatus.FINISHED);
        if (!allFinished) {
            return; // C'è ancora qualcuno che gioca! Aspettiamo.
        }
        // Il round è completato! Raccogliamo i vincitori mantenendo l'ordine
        List<User> winners = new ArrayList<>();
        for (Match m : currentRoundMatches) {
            if (m.getScorePlayer1() != null && m.getScorePlayer1() == 1) {
                winners.add(m.getPlayer1());
            } else if (m.getScorePlayer2() != null && m.getScorePlayer2() == 1) {
                winners.add(m.getPlayer2());
            }
        }
        // Se c'è solo 1 vincitore totale, IL TORNEO È FINITO!
        if (winners.size() == 1) {
            Tournament t = repo.findById(tournamentId).orElseThrow();
            t.setStatus("CONCLUSO");
            repo.save(t);
            return;
        }
        // Se ci sono più vincitori, creiamo i nuovi accoppiamenti (Es. Semifinali, Finale)
        Tournament t = repo.findById(tournamentId).orElseThrow();
        for (int i = 0; i < winners.size(); i += 2) {
            Match newMatch = new Match();
            newMatch.setTournament(t);
            newMatch.setRoundNumber(currentRound + 1);
            newMatch.setPlayer1(winners.get(i));
            newMatch.setPlayer2(winners.get(i + 1));
            newMatch.setStato(MatchStatus.PENDING);
            matchRepo.save(newMatch);
        }
    }
}

