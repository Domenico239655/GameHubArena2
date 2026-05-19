package com.gamehub.arena.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gameHubArena/gameHub")
public class AuthController {
    @GetMapping("/test")
    public String test(){
        return "Backend OK!";
    }
}