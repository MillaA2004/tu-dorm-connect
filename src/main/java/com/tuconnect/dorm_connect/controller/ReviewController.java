package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Review.ReviewRequestDTO;
import com.tuconnect.dorm_connect.dto.Review.ReviewResponseDTO;
import com.tuconnect.dorm_connect.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getReviewById(id));
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@RequestBody ReviewRequestDTO dto, Authentication authentication) {
        return ResponseEntity.ok(service.createReview(dto, authentication));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> update(
            @PathVariable Long id,
            @RequestBody ReviewRequestDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.updateReview(id, dto, authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dorm/{dormId}")
    public ResponseEntity<List<ReviewResponseDTO>> getByDorm(@PathVariable Long dormId) {
        return ResponseEntity.ok(service.getReviewsByDorm(dormId));
    }


}
