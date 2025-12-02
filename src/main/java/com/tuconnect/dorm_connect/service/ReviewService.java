package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Review.ReviewRequestDTO;
import com.tuconnect.dorm_connect.dto.Review.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {

    List<ReviewResponseDTO> getAllReviews();

    ReviewResponseDTO getReviewById(Long id);

    ReviewResponseDTO createReview(ReviewRequestDTO dto);

    ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto);

    void deleteReview(Long id);

    List<ReviewResponseDTO> getReviewsByDorm(Long dormId);

    List<ReviewResponseDTO> getReviewsByUser(Long userId);
}
