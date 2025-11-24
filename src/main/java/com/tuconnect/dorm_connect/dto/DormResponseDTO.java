package com.tuconnect.dorm_connect.dto;

public record DormResponseDTO(
        Long id,
        String name,
        String address,
        String blockNumber,
        String amenitiesJson,
        Double price,
        Double latitude,
        Double longitude
) {}
