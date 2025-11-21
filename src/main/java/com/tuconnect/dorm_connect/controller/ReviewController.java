package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.ReviewDTO;
import com.tuconnect.dorm_connect.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // -------------------------
    // GET ALL
    // -------------------------
    @GetMapping
    public List<ReviewDTO> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // -------------------------
    // GET BY ID
    // -------------------------
    @GetMapping("/{id}")
    public ReviewDTO getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    // -------------------------
    // CREATE REVIEW
    // -------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO createReview(@RequestBody ReviewDTO dto) {
        return reviewService.createReview(dto);
    }

    // -------------------------
    // UPDATE REVIEW
    // -------------------------
    @PutMapping("/{id}")
    public ReviewDTO updateReview(@PathVariable Long id, @RequestBody ReviewDTO updatedReviewDTO) {
        return reviewService.updateReview(id, updatedReviewDTO);
    }

    // -------------------------
    // DELETE REVIEW
    // -------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    // -------------------------
    // GET REVIEWS BY DORM
    // -------------------------
    @GetMapping("/dorm/{dormId}")
    public List<ReviewDTO> getReviewsByDorm(@PathVariable Long dormId) {
        return reviewService.getReviewsByDorm(dormId);
    }

    // -------------------------
    // GET REVIEWS BY USER
    // -------------------------
    @GetMapping("/user/{userId}")
    public List<ReviewDTO> getReviewsByUser(@PathVariable Long userId) {
        return reviewService.getReviewsByUser(userId);
    }
}
