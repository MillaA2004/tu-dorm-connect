package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.DormDTO;

import java.util.List;

public interface DormService {

    List<DormDTO> getAllDorms();
    DormDTO getDormById(Long id);
    DormDTO createDorm(DormDTO dormDTO);
    DormDTO updateDorm(Long id, DormDTO updatedDormDTO);
    void deleteDorm(Long id);

    DormDTO getDormByName(String name);
    List<DormDTO> getDormsByPriceMax(Double maxPrice);
    List<DormDTO> getDormsByBlock(String blockNumber);
    List<DormDTO> searchDorms(String keyword);
    List<DormDTO> getDormsByPriceRange(Double minPrice, Double maxPrice);
}
