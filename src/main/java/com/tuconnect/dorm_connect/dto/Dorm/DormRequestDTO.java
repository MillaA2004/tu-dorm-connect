package com.tuconnect.dorm_connect.dto.Dorm;

public record DormRequestDTO(
        String name,
        String address,
        String blockNumber,
        String amenitiesJson,
        Double price,
        Double latitude,
        Double longitude
) {}
