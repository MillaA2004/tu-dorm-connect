package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Review;
import com.tuconnect.dorm_connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByDormId(Long dormId);

    boolean existsByUserAndDorm(User user, Dorm dorm);

    @Query("""
           SELECT AVG(r.rating)
           FROM Review r
           WHERE r.dorm.id = :dormId
           """)
    Double findAverageRatingByDormId(@Param("dormId") Long dormId);


}
