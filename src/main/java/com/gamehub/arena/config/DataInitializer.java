package com.gamehub.arena.config;

import com.gamehub.arena.dao.GameRepository;
import com.gamehub.arena.dao.TournamentRepository;
import com.gamehub.arena.model.Game;
import com.gamehub.arena.model.Tournament;
import com.gamehub.arena.serviceImpl.RawgService;
import com.gamehub.arena.dto.GameExternalDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TournamentRepository tournamentRepository;
    private final GameRepository gameRepository;
    private final RawgService rawgService;

    public DataInitializer(TournamentRepository tournamentRepository,
                           GameRepository gameRepository,
                           RawgService rawgService) {
        this.tournamentRepository = tournamentRepository;
        this.gameRepository = gameRepository;
        this.rawgService = rawgService;
    }

    @Override
    public void run(String... args) throws Exception {

        // LA MODIFICA FONDAMENTALE: Controlliamo se il database è vuoto
        if (tournamentRepository.count() == 0) {

            System.out.println("=== IL DATABASE È VUOTO: AVVIO POPOLAMENTO INIZIALE DA RAWG ===");

            String[] giochiTarget = {"League of Legends", "Valorant", "Fortnite", "Counter-Strike 2"};

            for (String nomeGioco : giochiTarget) {
                List<GameExternalDTO> apiResults = rawgService.searchGames(nomeGioco);

                if (!apiResults.isEmpty()) {
                    GameExternalDTO rawgGame = apiResults.get(0);

                    // Creiamo il Gioco
                    Game game = new Game();
                    game.setTitle(rawgGame.getTitle());
                    game.setCoverUrl(rawgGame.getBackgroundImage());
                    game.setGenere(rawgGame.getGenere());
                    game.setRating(rawgGame.getRating());

                    game = gameRepository.save(game);

                    // Creiamo il Torneo
                    Tournament tournament = new Tournament();
                    tournament.setTitle(game.getTitle() + " - Arena Championship");
                    tournament.setStartDate(tournament.getStartDate());
                    tournament.setStatus("ISCRIZIONI_APERTE");
                    tournament.setDescription("Benvenuto al torneo ufficiale di " + game.getTitle() +
                            ". Dimostra le tue abilità, scala la classifica di GameHub Arena e vinci il premio finale!");
                    tournament.setGame(game);
                    tournament.setParticipants(new ArrayList<>());

                    tournamentRepository.save(tournament);
                    System.out.println("Inserito con successo il torneo per: " + game.getTitle());
                }
            }
            System.out.println("=== INIZIALIZZAZIONE COMPLETATA CON SUCCESSO ===");

        } else {
            // Se ci sono già dati, Spring Boot salta questo blocco e i tuoi dati reali rimangono al sicuro!
            System.out.println("=== DATABASE GIÀ POPOLATO: I tuoi tornei risiedono al sicuro nel DB ===");
        }
    }
}
