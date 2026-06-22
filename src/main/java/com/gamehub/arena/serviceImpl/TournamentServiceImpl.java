package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.*;
import com.gamehub.arena.dto.*;
import com.gamehub.arena.model.*;
import com.gamehub.arena.service.GameService;
import com.gamehub.arena.service.NotificationService;
import com.gamehub.arena.service.TournamentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // INIZIO: Logica creazione Tornei
    @Override
    public TournamentResponseDTO create(TournamentCreateDTO dto, String username) {
        // Recupera l'utente organizzatore che sta creando il torneo
        User organizer = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organizzatore non trovato!"));
        
        // Mappa il DTO (dati in ingresso) nell'entità Tournament
        Tournament t = fromDTO(dto);
        t.setOrganizer(organizer); // Assegna l'organizzatore
        
        // Salva il torneo nel database
        repo.save(t);
        
        // Ritorna la risposta mappata in DTO
        return toDTO(t);
    }
    // FINE: Logica creazione Tornei

    @Override
    public TournamentResponseDTO getById(Long id) {
        Tournament t = repo.findById(id)
                .orElseThrow(()->new RuntimeException("Torneo non trovato"));

        if ("ISCRIZIONI_APERTE".equals(t.getStatus()) && t.getStartDate() != null) {
            if (java.time.LocalDateTime.now().isAfter(t.getStartDate()) || java.time.LocalDateTime.now().isEqual(t.getStartDate())) {
                try {
                    this.generateBracket(t.getId());
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
            List<Match> matches = matchRepo.findByTournamentIdOrderByRoundNumberAsc(t.getId());

            List<TeamResponseDTO> mappedTeams = t.getParticipants().stream().map(user -> {
                TeamResponseDTO teamDto = new TeamResponseDTO();
                teamDto.setId(user.getId());
                teamDto.setName(user.getUsername());
                
                int score = 0;
                for (Match m : matches) {
                    if (m.getStato() == MatchStatus.FINISHED) {
                        if (m.getPlayer1() != null && m.getPlayer1().getId().equals(user.getId()) && m.getScorePlayer1() != null && m.getScorePlayer1() == 1) {
                            score += 10;
                        } else if (m.getPlayer2() != null && m.getPlayer2().getId().equals(user.getId()) && m.getScorePlayer2() != null && m.getScorePlayer2() == 1) {
                            score += 10;
                        }
                    }
                }
                
                teamDto.setScore(score);
                return teamDto;
            })
            .sorted((t1, t2) -> Integer.compare(t2.getScore(), t1.getScore()))
            .toList();

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
    @Transactional
    public void generateBracket(Long tournamentId) {
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
        if (size < 2) {
            throw new RuntimeException("Minimo 2 partecipanti necessari per il torneo!");
        }

        int nextPowerOfTwo = 1;
        while(nextPowerOfTwo < size) nextPowerOfTwo*=2;
        while(players.size() < nextPowerOfTwo){
            players.add(null);
        }
        for(int i = 0; i < players.size(); i += 2){
            Match m = new Match();
            m.setTournament(t);
            User p1 = players.get(i);
            User p2 = players.get(i + 1);
            m.setPlayer1(p1);
            m.setPlayer2(p2);
            m.setRoundNumber(1);

            if (p1 == null || p2 == null) {
                m.setStato(MatchStatus.FINISHED);
                if (p1 != null) {
                    m.setScorePlayer1(1);
                    m.setScorePlayer2(0);
                } else if (p2 != null) {
                    m.setScorePlayer1(0);
                    m.setScorePlayer2(1);
                }
            } else {
                m.setStato(MatchStatus.PENDING);
            }

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

    @Override
    public void savePlayerGameId(Long tournamentId, Long userId, String gameId) {
        Tournament t = repo.findById(tournamentId).orElseThrow(() -> new RuntimeException("Torneo non trovato"));
        User u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (playerIdRepo.findByTournamentAndUser(t, u).isPresent()) {
            throw new RuntimeException("Game ID già inserito per questo torneo!");
        }

        TournamentPlayerId tpi = new TournamentPlayerId();
        tpi.setTournament(t);
        tpi.setUser(u);
        tpi.setGameId(gameId);
        playerIdRepo.save(tpi);
    }

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
        if ("ISCRIZIONI_APERTE".equals(t.getStatus()) && t.getStartDate() != null) {
            if (java.time.LocalDateTime.now().isAfter(t.getStartDate()) || java.time.LocalDateTime.now().isEqual(t.getStartDate())) {
                try {
                    this.generateBracket(t.getId());
                } catch (Exception e) {
                System.err.println("Impossibile generare bracket: " + e.getMessage());
            }
            }
        }
        List<Match> matches = matchRepo.findByTournamentIdOrderByRoundNumberAsc(tournamentId);
        Match myMatch = null;
        User opponent = null;
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
        
        int size = t.getParticipants().size();
        if (size < 2) size = 2;
        int nextPowerOfTwo = 1;
        while(nextPowerOfTwo < size) nextPowerOfTwo*=2;
        int totalRounds = Integer.numberOfTrailingZeros(nextPowerOfTwo);
        
        response.put("matchId", myMatch.getId());
        response.put("roundNumber", myMatch.getRoundNumber());
        response.put("totalRounds", totalRounds);
        response.put("matchStatus", myMatch.getStato().name());
        boolean hasReported = false;
        boolean isWinner = false;
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

        response.put("myScreenshot", myMatch.getPlayer1() != null && myMatch.getPlayer1().getId().equals(userId) ? myMatch.getScreenshotPlayer1() : myMatch.getScreenshotPlayer2());
        response.put("opponentScreenshot", myMatch.getPlayer1() != null && myMatch.getPlayer1().getId().equals(userId) ? myMatch.getScreenshotPlayer2() : myMatch.getScreenshotPlayer1());

        if (opponent != null) {
            response.put("opponentName", opponent.getUsername());
            String opponentGameId = playerIdRepo.findByTournamentAndUser(t, opponent)
                    .map(TournamentPlayerId::getGameId).orElse("Non inserito");
            response.put("opponentGameId", opponentGameId);
        }
        return response;
    }

    @Override
    public String saveScreenshot(Long matchId, Long userId, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        Match m = matchRepo.findById(matchId).orElseThrow(() -> new RuntimeException("Match non trovato"));
        if (m.getStato() != MatchStatus.DISPUTED) {
            throw new RuntimeException("Puoi caricare uno screenshot solo se il match è contestato!");
        }

        java.nio.file.Path uploadPath = java.nio.file.Paths.get(System.getProperty("user.dir"), "uploads");
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        String filename = "match-" + matchId + "-user-" + userId + "-" + file.getOriginalFilename();
        java.nio.file.Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());

        String url = "/api/tournaments/uploads/" + filename;

        if (m.getPlayer1() != null && m.getPlayer1().getId().equals(userId)) {
            m.setScreenshotPlayer1(url);
        } else if (m.getPlayer2() != null && m.getPlayer2().getId().equals(userId)) {
            m.setScreenshotPlayer2(url);
        } else {
            throw new RuntimeException("Non fai parte di questo match!");
        }
        matchRepo.save(m);
        return url;
    }

    // INIZIO: Logica salvataggio risultato match
    @Override
    public void reportMatchResult(Long matchId, Long userId, boolean isWinner) {
        // 1. Recupera il match dal DB
        Match m = matchRepo.findById(matchId).orElseThrow(() -> new RuntimeException("Match non trovato"));
        
        // 2. Converte il booleano isWinner in punteggio (1 vittoria, 0 sconfitta)
        int score = isWinner ? 1 : 0;

        // 3. Assegna il punteggio al giocatore corretto
        if (m.getPlayer1() != null && m.getPlayer1().getId().equals(userId)) {
            m.setScorePlayer1(score);
        } else if (m.getPlayer2() != null && m.getPlayer2().getId().equals(userId)) {
            m.setScorePlayer2(score);
        } else {
            throw new RuntimeException("Non fai parte di questo match!");
        }
        
        // 4. Se ENTRAMBI i giocatori hanno inserito il risultato, verifichiamo la coerenza
        if (m.getScorePlayer1() != null && m.getScorePlayer2() != null) {
            // Se uno dichiara di aver vinto e l'altro di aver perso, il match è concluso regolarmente (FINISHED)
            if (m.getScorePlayer1() == 1 && m.getScorePlayer2() == 0) {
                m.setStato(MatchStatus.FINISHED);
            } else if (m.getScorePlayer2() == 1 && m.getScorePlayer1() == 0) {
                m.setStato(MatchStatus.FINISHED);
            } else {
                // Se entrambi dichiarano di aver vinto (o perso), il risultato è contestato (DISPUTED)
                m.setStato(MatchStatus.DISPUTED);
            }
        }
        
        // 5. Salva lo stato aggiornato del match
        matchRepo.save(m);
        
        // 6. Se il match si è concluso senza dispute, prova a generare il turno successivo
        if (m.getStato() == MatchStatus.FINISHED) {
            checkAndGenerateNextRound(m.getTournament().getId(), m.getRoundNumber());
        }
    }
    // FINE: Logica salvataggio risultato match

    private void checkAndGenerateNextRound(Long tournamentId, int currentRound) {
        List<Match> allMatches = matchRepo.findByTournamentIdOrderByRoundNumberAsc(tournamentId);
        List<Match> currentRoundMatches = allMatches.stream()
                .filter(m -> m.getRoundNumber() == currentRound)
                .toList();
        boolean allFinished = currentRoundMatches.stream()
                .allMatch(m -> m.getStato() == MatchStatus.FINISHED);
        if (!allFinished) {
            return;
        }
        List<User> winners = new ArrayList<>();
        for (Match m : currentRoundMatches) {
            if (m.getScorePlayer1() != null && m.getScorePlayer1() == 1) {
                winners.add(m.getPlayer1());
            } else if (m.getScorePlayer2() != null && m.getScorePlayer2() == 1) {
                winners.add(m.getPlayer2());
            }
        }
        if (winners.size() == 1) {
            Tournament t = repo.findById(tournamentId).orElseThrow();
            t.setStatus("CONCLUSO");

            User winner = winners.get(0);
            t.setWinner(winner);
            t.setEndDate(java.time.LocalDateTime.now());
            winner.setRank(winner.getRank() + 10);
            userRepo.save(winner);
            repo.save(t);
            return;
        }
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

    @Override
    public java.util.Map<String, Object> getPlayerOfTheMonth(){
        List<Object[]> results = repo.findPlayerofTheMonth();
        if(results.isEmpty()){return java.util.Map.of("username", "Nessuno", "points", 0);}
        Object[]top =  results.get(0);
        String username = (String) top[0];
        long wins = ((Number) top[1]).longValue();

        return java.util.Map.of("username", username, "points", wins*10);
    }
}

