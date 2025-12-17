package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingService {

    ListingResponseDTO createListing(ListingRequestDTO dto);

    ListingResponseDTO getListingById(Long id);

    List<ListingResponseDTO> getActiveListings();

    List<ListingResponseDTO> getListingsByUserId(Long userId);

    List<ListingResponseDTO> getListingsByDorm(String dormName);

    ListingResponseDTO updateListing(Long id, ListingRequestDTO dto, Long currentUserId);

    void deleteListing(Long id, Long currentUserId);

    List<ListingResponseDTO> searchListings(String keyword);

    List<ListingResponseDTO> getListingsByPriceMax(Double maxPrice);
}
