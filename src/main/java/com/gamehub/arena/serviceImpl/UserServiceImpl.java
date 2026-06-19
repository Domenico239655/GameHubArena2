package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.dto.*;
import com.gamehub.arena.model.Game;
import com.gamehub.arena.model.Role;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.GameService;
import com.gamehub.arena.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userDao;
    private final PasswordEncoder passwordEncoder;
    private final GameService gameService;

    public UserServiceImpl(UserRepository userDao, PasswordEncoder passwordEncoder, GameService gameService){
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.gameService = gameService;
    }

    @Override
    public UserResponseDTO register(UserCreateDTO dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        user.setRank(0);

        userDao.save(user);
        return toDTO(user);
    }

    @Override
    public UserResponseDTO login(UserLoginDTO dto){
        User user = userDao.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenziali Errate!"));
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new RuntimeException("Password errata!");
        return toDTO(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {

        return userDao.findByUsername(username);
    }

    public UserResponseDTO toDTO(User user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        
        if (user.getRole() != null) {
            dto.setRole(user.getRole().name());
        } else {
            dto.setRole("ROLE_PLAYER");
        }
        dto.setRank(user.getRank());
        return dto;
    }

    @Override
    @Transactional(readOnly=true)
    public List<GameResponseDTO> getLibrary(String username){
        User user = userDao.findByUsername(username).orElseThrow(() -> new RuntimeException("Utente non trovato"));
        return user.getPersonalLibrary().stream().map(gameService::toDTO).toList();
    }

    @Override
    @Transactional
    public void addGameToLibrary(String username,Long gameId){
        User user = userDao.findByUsername(username).orElseThrow(() -> new RuntimeException("Utente non trovato"));
        Game game = gameService.findEntityById(gameId).orElseThrow(() -> new RuntimeException("Gioco non trovato"));
        user.getPersonalLibrary().add(game);
        userDao.save(user);
    }

    @Override
    @Transactional
    public void removeGameFromLibrary(String username,Long gameId){
        User user = userDao.findByUsername(username).orElseThrow(() -> new RuntimeException("Utente non trovato"));
        boolean removed = user.getPersonalLibrary().removeIf(g -> g.getId().equals(gameId));
        if(!removed){
            throw new  RuntimeException("Gioco non presente nella libreria");
        }
        userDao.save(user);
    }

}
