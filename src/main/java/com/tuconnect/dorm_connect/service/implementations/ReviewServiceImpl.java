package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Review.ReviewRequestDTO;
import com.tuconnect.dorm_connect.dto.Review.ReviewResponseDTO;
import com.tuconnect.dorm_connect.mapper.ReviewMapper;
import com.tuconnect.dorm_connect.model.Review;
import com.tuconnect.dorm_connect.repository.DormRepository;
import com.tuconnect.dorm_connect.repository.ReviewRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final DormRepository dormRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper,UserRepository userRepository,DormRepository dormRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.userRepository=userRepository;
        this.dormRepository=dormRepository;
    }

    @Override
    @Transactional
    public ReviewResponseDTO createReview(
            ReviewRequestDTO dto,
            Authentication authentication
    ) {
        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var dorm = dormRepository.findById(dto.dormId())
                .orElseThrow(() -> new IllegalArgumentException("Dorm not found"));


        if (dto.rating() == null || dto.rating() < 1 || dto.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }


        if (reviewRepository.existsByUserAndDorm(user, dorm)) {
            throw new IllegalStateException("You already reviewed this dorm");
        }

        Review review = reviewMapper.toEntity(dto);
        review.setUser(user);
        review.setDorm(dorm);
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return reviewMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto, Authentication authentication) {

        var existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!existingReview.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not allowed to edin other users reviews!");
        }

        if (dto.rating() == null || dto.rating() < 1 || dto.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        existingReview.setComment(dto.comment());
        existingReview.setRating(dto.rating());


        Review saved = reviewRepository.save(existingReview);
        return reviewMapper.toDTO(saved);

    }

    @Override
    public ReviewResponseDTO getReviewById(Long id) {
        Review rev = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        return reviewMapper.toDTO(rev);
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
    @Transactional
    public Double getAverageRatingForDorm(Long dormId) {

        if (!dormRepository.existsById(dormId)) {
            throw new EntityNotFoundException("Dorm not found");
        }

        Double avg = reviewRepository.findAverageRatingByDormId(dormId);
        return avg != null ? avg : 0.0;
    }


}
