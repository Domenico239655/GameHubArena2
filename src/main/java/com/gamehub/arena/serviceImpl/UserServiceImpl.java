package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.dto.RegisterRequest;
import com.gamehub.arena.dto.UserCreateDTO;
import com.gamehub.arena.dto.UserLoginDTO;
import com.gamehub.arena.dto.UserResponseDTO;
import com.gamehub.arena.model.Role;
import com.gamehub.arena.model.User;
import com.gamehub.arena.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Repository
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userDao, PasswordEncoder passwordEncoder){
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO register(UserCreateDTO dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.PLAYER);
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
        dto.setRole(user.getRole().name());
        dto.setRank(user.getRank());
        return dto;
    }

}
