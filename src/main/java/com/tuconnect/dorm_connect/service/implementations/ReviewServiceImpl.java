package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Review.ReviewRequestDTO;
import com.tuconnect.dorm_connect.dto.Review.ReviewResponseDTO;
import com.tuconnect.dorm_connect.mapper.ReviewMapper;
import com.tuconnect.dorm_connect.model.Review;
import com.tuconnect.dorm_connect.repository.ReviewRepository;
import com.tuconnect.dorm_connect.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {

        if (dto.userId() == null)
            throw new IllegalArgumentException("userId is required");

        if (dto.dormId() == null)
            throw new IllegalArgumentException("dormId is required");

        Review review = reviewMapper.toEntity(dto);
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);

        return reviewMapper.toDTO(saved);
    }

    @Override
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto) {

        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        Review updated = reviewMapper.toEntity(dto);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt()); // запазваме createdAt

        Review saved = reviewRepository.save(updated);

        return reviewMapper.toDTO(saved);
    }

    @Override
    public ReviewResponseDTO getReviewById(Long id) {
        Review rev = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        return reviewMapper.toDTO(rev);
    }

    @Override
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteReview(Long id) {
        Review rev = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        reviewRepository.delete(rev);
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByDorm(Long dormId) {
        return reviewRepository.findByDormId(dormId)
                .stream()
                .map(reviewMapper::toDTO)
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(reviewMapper::toDTO)
                .toList();
    }
}
