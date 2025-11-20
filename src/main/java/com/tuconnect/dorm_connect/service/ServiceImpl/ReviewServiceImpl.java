package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.ReviewDTO;
import com.tuconnect.dorm_connect.mapper.ReviewMapper;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Review;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.ReviewRepository;
import com.tuconnect.dorm_connect.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        return reviewMapper.toDTO(review);
    }

    @Override
    public ReviewDTO createReview(ReviewDTO dto) {

        // Проверка: userId и dormId са задължителни
        if (dto.userId() == null) {
            throw new RuntimeException("Cannot create review: userId is required");
        }
        if (dto.dormId() == null) {
            throw new RuntimeException("Cannot create review: dormId is required");
        }

        Review review = new Review();
        review.setRating(dto.rating());
        review.setComment(dto.comment());
        review.setCategoryScoresJson(dto.categoryScoresJson());
        review.setCreatedAt(LocalDateTime.now());

        // Set user relation
        User user = new User();
        user.setUserId(dto.userId());
        review.setUser(user);

        // Set dorm relation
        Dorm dorm = new Dorm();
        dorm.setId(dto.dormId());
        review.setDorm(dorm);

        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toDTO(savedReview);
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewDTO updatedReviewDTO) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        Review updatedEntity = reviewMapper.toEntity(updatedReviewDTO);
        updatedEntity.setId(existing.getId());

        Review saved = reviewRepository.save(updatedEntity);
        return reviewMapper.toDTO(saved);
    }

    @Override
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewDTO> getReviewsByDorm(Long dormId) {
        return reviewRepository.findByDormId(dormId)
                .stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserUserId(userId)
                .stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }
}
