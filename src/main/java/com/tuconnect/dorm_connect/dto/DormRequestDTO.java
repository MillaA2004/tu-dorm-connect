package com.tuconnect.dorm_connect.dto;

public record DormRequestDTO(
        String name,
        String address,
        String blockNumber,
        String amenitiesJson,
        Double price,
        Double latitude,
        Double longitude
) {}
