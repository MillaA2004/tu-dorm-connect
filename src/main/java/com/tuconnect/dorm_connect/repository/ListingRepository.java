package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    boolean existsByUserIdAndIsActiveTrue(Long userId);
    List<Listing> findByIsActiveTrueAndExpiresAtAfter(LocalDateTime now);

    Page<Listing> findByUserId(Long userId, Pageable pageable);

    Page<Listing> findByDormIdAndIsActiveTrueAndExpiresAtAfter(
            Long dormId,
            LocalDateTime now,
            Pageable pageable
    );

    @Query("SELECT l FROM Listing l WHERE " +
            "(LOWER(l.title) LIKE %:keyword% OR LOWER(l.description) LIKE %:keyword%) " +
            "AND l.isActive = true AND l.expiresAt > :now")
    Page<Listing> searchByKeyword(
            @Param("keyword") String keyword,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("SELECT l FROM Listing l WHERE " +
            "(LOWER(l.title) LIKE %:keyword% OR LOWER(l.description) LIKE %:keyword%) " +
            "AND l.dorm.id = :dormId AND l.isActive = true AND l.expiresAt > :now")
    Page<Listing> searchByKeywordAndDorm(
            @Param("keyword") String keyword,
            @Param("dormId") Long dormId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}
