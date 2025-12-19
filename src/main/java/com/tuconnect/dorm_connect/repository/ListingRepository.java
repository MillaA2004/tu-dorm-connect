package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    boolean existsByPosterIdAndIsActiveTrue(Long posterId);

    List<Listing> findByIsActiveTrueAndExpiresAtAfter(LocalDateTime now);

    List<Listing> findByPosterIdAndIsActiveTrueAndExpiresAtAfter(
            Long posterId,
            LocalDateTime now
    );

    List<Listing> findByDormAndIsActiveTrueAndExpiresAtAfter(
            String dorm,
            LocalDateTime now
    );

    @Query("SELECT l FROM Listing l WHERE " +
            "(LOWER(l.title) LIKE %:keyword% OR LOWER(l.description) LIKE %:keyword% OR LOWER(l.dorm) LIKE %:keyword%) " +
            "AND l.isActive = true AND l.expiresAt > :now")
    List<Listing> searchByKeyword(
            @Param("keyword") String keyword,
            @Param("now") LocalDateTime now
    );

    List<Listing> findByIsActiveTrueAndExpiresAtAfterAndPriceLessThanEqual(LocalDateTime now, Double maxPrice);
}
