package com.tuconnect.dorm_connect.dto.Dorm;

import com.tuconnect.dorm_connect.dto.Review.ReviewResponseDTO;

import java.util.List;

public record DormResponseDTO(
        Long id,
        String name,
        String address,
        String description,
        Double price,
        List<String> imageUrlsList,
        Double latitude,
        Double longitude,
        List<ReviewResponseDTO> reviews
) {}
