package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dao.ReviewRepository;
import com.gamehub.arena.model.Review;
import com.gamehub.arena.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repo;

    public ReviewServiceImpl(ReviewRepository repo){
        this.repo = repo;
    }

    @Override
    public Review create(Review r) {
        return repo.save(r);
    }

    @Override
    public List<Review> getAll() {
        return repo.findAll();
    }
}
