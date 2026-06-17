package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.GameRepository;
import com.gamehub.arena.dao.ReviewRepository;
import com.gamehub.arena.dao.TournamentRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.dto.ReviewCreateDTO;
import com.gamehub.arena.dto.ReviewResponseDTO;
import com.gamehub.arena.model.Game;
import com.gamehub.arena.model.Review;
import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repo;
    private final UserRepository userRepo;
    private final GameRepository gameRepo;
    private final TournamentRepository tournamentRepo;

    public ReviewServiceImpl(ReviewRepository repo,
                             UserRepository userRepo, GameRepository gameRepo,
                             TournamentRepository tournamentRepo){
        this.repo = repo;
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.tournamentRepo = tournamentRepo;
    }

    @Override
    public ReviewResponseDTO create(ReviewCreateDTO dto) {
        Review review = fromDTO(dto);
        repo.save(review);
        return toDTO(review);
    }

    @Override
    public List<ReviewResponseDTO> getAll() {

        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public ReviewResponseDTO getById(Long id) {
        Review review = repo.findById(id)
                .orElseThrow(()-> new RuntimeException("Review non trovata!"));
        return toDTO(review);
    }

    @Override
    public ReviewResponseDTO toDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUsername(review.getUser().getUsername());
        dto.setGameName(review.getGame() != null ? review.getGame().getTitle():null);
        dto.setTournamentTitle(review.getTournament() != null ? review.getTournament().getTitle() : null);
        return dto;
    }

    @Override
    public Review fromDTO(ReviewCreateDTO dto) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        User user= userRepo.findById(dto.getUserId())
                .orElseThrow(()->new RuntimeException("Utente non trovato!"));
        review.setUser(user);

        if(dto.getGameId() != null){
            Game game = gameRepo.findById(dto.getGameId())
                    .orElseThrow(()->new RuntimeException("Gioco non trovato!"));
            review.setGame(game);
        }

        if(dto.getTournamentId() != null){
            Tournament tournament = tournamentRepo.findById(dto.getTournamentId())
                    .orElseThrow(()-> new RuntimeException("Torneo non trovato!"));
            review.setTournament(tournament);
        }
        return review;
    }

    @Override
    public Optional<Review> findEntityById(Long id) {
        return repo.findById(id);
    }

    @Override
    public ReviewResponseDTO createWithUser(ReviewCreateDTO dto, String username) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato per lo username: " + username));
        review.setUser(user);

        if (dto.getGameId() != null) {
            Game game = gameRepo.findById(dto.getGameId())
                    .orElseThrow(() -> new RuntimeException("Gioco non trovato!"));
            review.setGame(game);
        }

        if (dto.getTournamentId() != null) {
            Tournament tournament = tournamentRepo.findById(dto.getTournamentId())
                    .orElseThrow(() -> new RuntimeException("Torneo non trovato!"));
            review.setTournament(tournament);
        }

        repo.save(review);
        return toDTO(review);
    }

}
