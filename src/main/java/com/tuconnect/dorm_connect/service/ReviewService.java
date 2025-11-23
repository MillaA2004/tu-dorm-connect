package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.ReviewRequestDTO;
import com.tuconnect.dorm_connect.dto.ReviewResponseDTO;

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
