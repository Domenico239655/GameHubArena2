package com.gamehub.arena.service;

import com.gamehub.arena.dto.ReviewCreateDTO;
import com.gamehub.arena.dto.ReviewResponseDTO;
import com.gamehub.arena.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewResponseDTO create(ReviewCreateDTO dto);
    List<ReviewResponseDTO> getAll();
    ReviewResponseDTO getById(Long id);
    ReviewResponseDTO toDTO(Review review);
    Review fromDTO(ReviewCreateDTO dto);
    Optional<Review> findEntityById(Long id);
}
