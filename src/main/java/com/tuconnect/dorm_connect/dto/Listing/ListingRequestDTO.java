package com.tuconnect.dorm_connect.dto.Listing;

public record ListingRequestDTO(
        String title,
        String description,
        Double price,
        Long dormId,
        Long userId
) {
}
