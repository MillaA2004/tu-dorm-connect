package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListingService {

    ListingResponseDTO createListing(ListingRequestDTO dto);

    ListingResponseDTO getListingById(Long id);

    //Page<ListingResponseDTO> getActiveListings(Pageable pageable);

    //Page<ListingResponseDTO> getMyListings(Long userId, Pageable pageable);

    ListingResponseDTO updateListing(Long id, ListingRequestDTO dto, Long currentUserId);

    void deleteListing(Long id, Long currentuserId);

    //Page<ListingResponseDTO> searchListing(String keyword, Long dormId, Pageable pageable);
}
