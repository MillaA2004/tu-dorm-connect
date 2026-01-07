package com.tuconnect.dorm_connect.dto.Dorm;

public record DormUpdateRequestDTO(
        String name,
        String address,
        String description,
        Double price,
        Double latitude,
        Double longitude,
        Boolean replaceImages
) {}

