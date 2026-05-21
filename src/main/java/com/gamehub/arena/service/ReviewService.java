package com.gamehub.arena.service;

import com.gamehub.arena.model.Review;

import java.util.List;

public interface ReviewService {
    Review create(Review r);
    List<Review> getAll();
}
