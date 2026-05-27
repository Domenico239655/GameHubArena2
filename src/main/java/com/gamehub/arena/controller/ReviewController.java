package com.gamehub.arena.controller;

import com.gamehub.arena.dto.ReviewCreateDTO;
import com.gamehub.arena.dto.ReviewResponseDTO;
import com.gamehub.arena.model.Review;
import com.gamehub.arena.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service){
        this.service = service;
    }

    @PostMapping
    public ReviewResponseDTO create(@RequestBody ReviewCreateDTO r){
        return service.create(r);
    }

    @GetMapping
    public List<ReviewResponseDTO> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReviewResponseDTO getById(@PathVariable Long id){
        return service.getById(id);
    }
}
