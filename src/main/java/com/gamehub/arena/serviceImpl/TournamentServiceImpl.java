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

    public TournamentServiceImpl(TournamentRepository repo, UserRepository userRepo, GameRepository gameRepo, NotificationService notificationService, MatchRepository matchRepo, GameService gameService, TournamentRatingRepository ratingRepo){
        this.repo = repo;
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.notificationService = notificationService;
        this.matchRepo = matchRepo;
        this.gameService = gameService;
        this.ratingRepo = ratingRepo;
    }

    @Override
    public TournamentResponseDTO create(TournamentCreateDTO dto) {
        Tournament t = fromDTO(dto);
        repo.save(t);
        return toDTO(t);
    }

    @Override
    public TournamentResponseDTO getById(Long id) {
        Tournament t = repo.findById(id)
                .orElseThrow(()->new RuntimeException("Torneo non trovato"));
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

        t.getParticipants().add(u);
        repo.save(t);

        NotificationCreateDTO dtoUser = new NotificationCreateDTO();
        dtoUser.setUserId(userId);
        dtoUser.setMessage("Ti sei iscritto al torneo: " + t.getTitle());
        notificationService.send(dtoUser);

        NotificationCreateDTO dtoOrg = new NotificationCreateDTO();
        dtoOrg.setUserId(t.getOrganizer().getId());
        dtoOrg.setMessage("Nuovo iscitto al tuo torneo: " + u.getUsername());
        notificationService.send(dtoOrg);

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
        if (t.getRegistrationDeadline() != null){
            boolean isOpen = LocalDate.now().isBefore(t.getRegistrationDeadline()) || LocalDate.now().isEqual(t.getRegistrationDeadline());
            dto.setRegistrationOpen(isOpen);
        } else { dto.setRegistrationOpen(true);}

        dto.setRating(t.getRating() != null ? t.getRating() : 0.0);

        return dto;
    }

    @Override
    public Tournament fromDTO(TournamentCreateDTO dto){
        Tournament t = new Tournament();
        t.setTitle(dto.getTitle());
        t.setRegistrationDeadline(dto.getRegistrationDeadLine());

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
    public void generateBracket(Long tournamentId) {
        Tournament t = repo.findById(tournamentId)
                .orElseThrow(()-> new RuntimeException("Torneo non trovato!"));
        List<User> players = new ArrayList<>(t.getParticipants());

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


}

