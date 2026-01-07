package com.tuconnect.dorm_connect.dto.Dorm;

import java.util.List;

public record DormRequestDTO(
        String name,
        String address,
        String description,
        Double price,
        List<String> imageUrlsList,
        Double latitude,
        Double longitude
) {}
