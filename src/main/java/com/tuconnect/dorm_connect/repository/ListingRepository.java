package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    boolean existsByUserIdAndIsActiveTrue(Long userId);
    List<Listing> findByIsActiveTrueAndExpiresAtAfter(LocalDateTime now);
}
