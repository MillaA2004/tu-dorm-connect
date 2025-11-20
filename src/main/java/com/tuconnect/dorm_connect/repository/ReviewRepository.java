package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // всички ревюта за даден dorm
    List<Review> findByDormId(Long dormId);

    // всички ревюта за даден user
    List<Review> findByUserUserId(Long userId);

    // всички ревюта с даден рейтинг
    List<Review> findByRating(Integer rating);

    // ревюта съдържащи дума в коментара
    List<Review> findByCommentContainingIgnoreCase(String keyword);
}
