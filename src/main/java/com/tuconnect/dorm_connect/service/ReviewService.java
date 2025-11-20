package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllReviews();
    ReviewDTO getReviewById(Long id);
    ReviewDTO createReview(ReviewDTO reviewDTO);
    ReviewDTO updateReview(Long id, ReviewDTO updatedReviewDTO);
    void deleteReview(Long id);

    List<ReviewDTO> getReviewsByDorm(Long dormId);
    List<ReviewDTO> getReviewsByUser(Long userId);
}
