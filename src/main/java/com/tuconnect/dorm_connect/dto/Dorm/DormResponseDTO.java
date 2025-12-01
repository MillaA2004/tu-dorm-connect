package com.tuconnect.dorm_connect.dto.Dorm;

import com.tuconnect.dorm_connect.dto.User.UserSummaryDTO;

import java.util.List;

public record DormResponseDTO(
        Long id,
        String name,
        String address,
        String blockNumber,
        List<UserSummaryDTO> livingPeople,
        String amenitiesJson,
        Double price,
        Double latitude,
        Double longitude
) {}
