package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Review.ReviewRequestDTO;
import com.tuconnect.dorm_connect.dto.Review.ReviewResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ReviewService {

    void deleteReview(Long id);

    List<ReviewResponseDTO> getReviewsByDorm(Long dormId);

    Double getAverageRatingForDorm(Long dormId);

    ReviewResponseDTO getReviewById(Long id);

    ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto, Authentication authentication);

    ReviewResponseDTO createReview(ReviewRequestDTO dto, Authentication authentication);

}
