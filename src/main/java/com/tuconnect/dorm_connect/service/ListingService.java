package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingService {

    ListingResponseDTO createListing(Long posterId, ListingRequestDTO dto);

    ListingResponseDTO getListingById(Long id);

    List<ListingResponseDTO> getActiveListings();

    public List<ListingResponseDTO> getCompatibleListings(Long viewerId);

    List<ListingResponseDTO> getListingsByUserId(Long posterId);

    List<ListingResponseDTO> getListingsByDorm(String dormName);

    ListingResponseDTO updateListing(Long id, ListingRequestDTO dto, Long currentUserId);

    void deleteListing(Long id, Long currentUserId);

    List<ListingResponseDTO> searchListings(String keyword, Long viewerId);

    List<ListingResponseDTO> getListingsByPriceMax(Double maxPrice);
}
