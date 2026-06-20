package com.gamehub.arena.controller;

import com.gamehub.arena.dao.TournamentRepository;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.model.Role;
import com.gamehub.arena.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;


    public AdminController(UserRepository userRepository, TournamentRepository tournamentRepository){
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getDashboardStats() {

        Map<String, Integer> stats = new HashMap<>();
        int totalUsers =(int) userRepository.count();
        int activeTournaments = (int) tournamentRepository.count();

        int bannedUsers = (int)userRepository.countByRole(Role.BANNED);


        stats.put("totalUsers", totalUsers);
        stats.put("activeTournaments", activeTournaments);
        stats.put("bannedUsers", bannedUsers);

        return ResponseEntity.ok(stats);
    }
    @PutMapping("/promote-organizer/{username}")
    public ResponseEntity<?> promoteToOrganizer(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore: Utente '" + username + "' non trovato!");
        }

        User user = userOptional.get();

        if (Role.ORGANIZER.equals(user.getRole())) {
            return ResponseEntity.badRequest().body("L'utente è già un Organizzatore.");
        }

        if (Role.ADMIN.equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Operazione negata: L'utente è un ADMIN.");
        }

        user.setRole(Role.ORGANIZER);
        userRepository.save(user);

        return ResponseEntity.ok("Utente " + username + " promosso ad ORGANIZER con successo!");
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username, Authentication authentication) {
        try{
            String currentAdminUsername = authentication.getName();
            Optional<User> targetUserOpt = userRepository.findByUsername(username);

            if (targetUserOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Errore: Utente '" + username + "' non trovato nel database.");
            }

            User targetUser = targetUserOpt.get();

            if (currentAdminUsername.equals(targetUser.getUsername())) {
                userRepository.delete(targetUser);
                return ResponseEntity.ok("SELF_DELETED");
            }
            if("ADMIN".equals(targetUser.getRole())){
                return ResponseEntity.badRequest().body("OPERAZIONE NEGATA!");
            }

            userRepository.delete(targetUser);
            return ResponseEntity.ok("L'UTENTE "+ username + "E' STATO ELIMINATO DEFINITIVAMENTE DAL SISTEMA");


        }catch (Exception e){
            System.err.println("🚨 ERRORE DURANTE L'ELIMINAZIONE DELL'UTENTE: 🚨");
            e.printStackTrace();

            return ResponseEntity.internalServerError().body("ERRORE DEL DATABASE: " + e.getMessage());
        }
    }

    @PutMapping("/ban/{username}")
    public ResponseEntity<?> banUser(@PathVariable String username) {
        try {
            Optional<User> targetUserOpt = userRepository.findByUsername(username);

            if (targetUserOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Errore: Utente '" + username + "' non trovato.");
            }

            User targetUser = targetUserOpt.get();

            if ("ADMIN".equals(targetUser.getRole())) {
                return ResponseEntity.badRequest().body("Operazione negata: Non puoi bannare un Amministratore!");
            }

            if ("BANNED".equals(targetUser.getRole())) {
                return ResponseEntity.badRequest().body("L'utente '" + username + "' è già stato bannato in precedenza.");
            }

            targetUser.setRole(Role.valueOf("BANNED"));
            userRepository.save(targetUser);

            return ResponseEntity.ok("BAN HAMMER COLPITO! L'utente " + username + " è stato bannato con successo.");

        } catch (Exception e) {
            System.err.println("🚨 ERRORE DURANTE IL BAN DELL'UTENTE: 🚨");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("ERRORE DEL DATABASE: " + e.getMessage());
        }
    }
    @PutMapping("/unban/{username}")
    public ResponseEntity<?> unbanUser(@PathVariable String username) {
        try {
            Optional<User> targetUserOpt = userRepository.findByUsername(username);

            if (targetUserOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Errore: Utente '" + username + "' non trovato.");
            }

            User targetUser = targetUserOpt.get();

            if (!Role.BANNED.equals(targetUser.getRole())) {
                return ResponseEntity.badRequest().body("L'utente '" + username + "' non è attualmente bannato.");
            }

            targetUser.setRole(Role.PLAYER);
            userRepository.save(targetUser);

            return ResponseEntity.ok("RIPRISTINO COMPLETATO: L'utente " + username + " è stato riammesso nel sistema.");

        } catch (Exception e) {
            System.err.println("🚨 ERRORE DURANTE IL RIMUOVI BAN: 🚨");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("ERRORE DEL DATABASE: " + e.getMessage());
        }
    }
}
