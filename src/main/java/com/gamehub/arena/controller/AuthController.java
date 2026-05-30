package com.gamehub.arena.controller;

import com.gamehub.arena.dto.*;
import com.gamehub.arena.model.User;
import com.gamehub.arena.security.JwtUtil;
import com.gamehub.arena.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserCreateDTO dto) {
        UserResponseDTO user = userService.register(dto);

        UserDetails userDetail = new org.springframework.security.core.userdetails.User(user.getUsername(),
                dto.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );

        String token = jwtUtil.generateToken(userDetail);
        AuthResponse res = new AuthResponse();
        res.setUsername(user.getUsername());
        res.setRole(user.getRole());
        res.setToken(token);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginDTO dto) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        AuthResponse res = new AuthResponse();
        res.setUsername(userDetails.getUsername());
        res.setRole(userDetails.getAuthorities().iterator().next().getAuthority());
        res.setToken(token);

        return ResponseEntity.ok(res);
    }
}
