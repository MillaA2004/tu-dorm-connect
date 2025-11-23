package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.DormRequestDTO;
import com.tuconnect.dorm_connect.dto.DormResponseDTO;

import java.util.List;

public interface DormService {

    List<DormResponseDTO> getAllDorms();

    DormResponseDTO getDormById(Long id);

    DormResponseDTO createDorm(DormRequestDTO dto);

    DormResponseDTO updateDorm(Long id, DormRequestDTO dto);

    void deleteDorm(Long id);

    DormResponseDTO getDormByName(String name);

    List<DormResponseDTO> getDormsByPriceMax(Double maxPrice);

    List<DormResponseDTO> getDormsByBlock(String blockNumber);

    List<DormResponseDTO> searchDorms(String keyword);

    List<DormResponseDTO> getDormsByPriceRange(Double minPrice, Double maxPrice);
}
