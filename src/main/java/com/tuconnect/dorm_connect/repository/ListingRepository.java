package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
}
