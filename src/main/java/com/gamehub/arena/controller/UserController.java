package com.gamehub.arena.controller;

import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.dto.ChangePasswordDTO;
import com.gamehub.arena.dto.GameResponseDTO;
import com.gamehub.arena.dto.UserResponseDTO;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200" )
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserService service;


    public UserController(UserService service, UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.service = service;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(service::toDTO)
                .toList();
    }

    @GetMapping("/{username}")
    public UserResponseDTO getByUsername(@PathVariable String username){
        User user = service.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Utente non trovato!"));
        return service.toDTO(user);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO dto, Authentication authentication){
        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utente non trovato!"));

        if(!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())){
            return ResponseEntity.badRequest().body("La vecchia password non corrisponde!");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password aggiornata con successo!");
    }

    @GetMapping("/me/library")
    public List<GameResponseDTO> getMyLibrary(Authentication authentication){
        return service.getLibrary(authentication.getName());
    }

    @PostMapping("/me/library/{gameId}")
    public ResponseEntity<?> addToMyLibrary(@PathVariable Long gameId, Authentication authentication){
        service.addGameToLibrary(authentication.getName(), gameId);
        return ResponseEntity.ok("Gioco aggiunto dalla Libreria");
    }

    @DeleteMapping("/me/library/{gameId}")
    public ResponseEntity<?> removeFromMyLibrary(@PathVariable Long gameId, Authentication authentication){
        service.removeGameFromLibrary(authentication.getName(), gameId);
        return ResponseEntity.ok("Gioco rimosso dalla Libreria");
    }
}
