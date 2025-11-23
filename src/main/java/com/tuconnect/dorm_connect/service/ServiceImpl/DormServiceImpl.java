package com.tuconnect.dorm_connect.service.ServiceImpl;


import com.tuconnect.dorm_connect.dto.DormRequestDTO;
import com.tuconnect.dorm_connect.dto.DormResponseDTO;
import com.tuconnect.dorm_connect.mapper.DormMapper;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.repository.DormRepository;
import com.tuconnect.dorm_connect.service.DormService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormServiceImpl implements DormService {

    private final DormRepository dormRepository;
    private final DormMapper dormMapper;

    
    public DormServiceImpl(DormRepository dormRepository, DormMapper dormMapper) {
        this.dormRepository = dormRepository;
        this.dormMapper = dormMapper;
    }

    @Override
    public List<DormResponseDTO> getAllDorms() {
        return dormRepository.findAll().stream()
                .map(dormMapper::toDTO)
                .toList();
    }

    @Override
    public DormResponseDTO getDormById(Long id) {
        Dorm dorm = dormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dorm not found"));
        return dormMapper.toDTO(dorm);
    }

    @Override
    public DormResponseDTO createDorm(DormRequestDTO dto) {
        Dorm dorm = dormMapper.toEntity(dto);
        Dorm saved = dormRepository.save(dorm);
        return dormMapper.toDTO(saved);
    }

    @Override
    public DormResponseDTO updateDorm(Long id, DormRequestDTO dto) {
        Dorm existing = dormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dorm not found"));

        Dorm updated = dormMapper.toEntity(dto);
        updated.setId(existing.getId());

        Dorm saved = dormRepository.save(updated);
        return dormMapper.toDTO(saved);
    }

    @Override
    public void deleteDorm(Long id) {
        Dorm dorm = dormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dorm not found"));
        dormRepository.delete(dorm);
    }

    @Override
    public DormResponseDTO getDormByName(String name) {
        Dorm dorm = dormRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Dorm not found"));
        return dormMapper.toDTO(dorm);
    }

    @Override
    public List<DormResponseDTO> getDormsByPriceMax(Double maxPrice) {
        return dormRepository.findByPriceLessThanEqual(maxPrice)
                .stream()
                .map(dormMapper::toDTO)
                .toList();
    }

    @Override
    public List<DormResponseDTO> getDormsByBlock(String blockNumber) {
        return dormRepository.findByBlockNumber(blockNumber)
                .stream()
                .map(dormMapper::toDTO)
                .toList();
    }

    @Override
    public List<DormResponseDTO> searchDorms(String keyword) {
        return dormRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(dormMapper::toDTO)
                .toList();
    }

    @Override
    public List<DormResponseDTO> getDormsByPriceRange(Double minPrice, Double maxPrice) {
        return dormRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(dormMapper::toDTO)
                .toList();
    }
}
