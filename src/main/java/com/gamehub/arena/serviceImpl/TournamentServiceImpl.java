package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.GameRepository;
import com.gamehub.arena.dao.MatchRepository;
import com.gamehub.arena.dao.TournamentRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.dto.NotificationCreateDTO;
import com.gamehub.arena.dto.TournamentCreateDTO;
import com.gamehub.arena.dto.TournamentResponseDTO;
import com.gamehub.arena.model.*;
import com.gamehub.arena.service.NotificationService;
import com.gamehub.arena.service.TournamentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository repo;
    private final UserRepository userRepo;
    private final GameRepository gameRepo;
    private final NotificationService notificationService;
    private final MatchRepository matchRepo;

    public TournamentServiceImpl(TournamentRepository repo, UserRepository userRepo, GameRepository gameRepo, NotificationService notificationService, MatchRepository matchRepo){
        this.repo = repo;
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.notificationService = notificationService;
        this.matchRepo = matchRepo;
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
        dto.setGame(t.getGame().getTitle());
        dto.setDate(t.getDate());
        dto.setParticipantsCount(t.getParticipants().size());
        return dto;
    }

    @Override
    public Tournament fromDTO(TournamentCreateDTO dto){
        Tournament t = new Tournament();
        t.setTitle(dto.getTitle());
        t.setDate(dto.getDate());

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
        List<User> players = t.getParticipants();

        int size = players.size();
        int nextPowerOfTwo = 1;
        while(nextPowerOfTwo < size) nextPowerOfTwo*=2;
        while(players.size() < nextPowerOfTwo){
            players.add(null);
        }
        for(int i = 0; i < players.size(); i++){
            Match m = new Match();
            m.setTournament(t);
            m.setPlayer1(players.get(i));
            m.setPlayer2(players.get(i + 2));
            m.setRoundNumber(1);
            m.setStato(MatchStatus.PENDING);

            matchRepo.save(m);
        }
    }


}

