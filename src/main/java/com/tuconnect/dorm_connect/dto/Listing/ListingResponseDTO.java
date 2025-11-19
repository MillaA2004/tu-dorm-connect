package com.tuconnect.dorm_connect.dto.Listing;

public record ListingResponseDTO(
        Long id,
        String title,
        String description,
        Double price,
        Long dormId,
        String dormName,
        Long userId,
        String username
) {
}
