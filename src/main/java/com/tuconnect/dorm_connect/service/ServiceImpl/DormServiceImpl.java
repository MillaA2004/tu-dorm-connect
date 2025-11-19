package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.DormDTO;
import com.tuconnect.dorm_connect.mapper.DormMapper;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.repository.DormRepository;
import com.tuconnect.dorm_connect.service.DormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DormServiceImpl implements DormService {

    private final DormRepository dormRepository;
    private final DormMapper dormMapper;

    @Autowired
    public DormServiceImpl(DormRepository dormRepository, DormMapper dormMapper) {
        this.dormRepository = dormRepository;
        this.dormMapper = dormMapper;
    }

    @Override
    public List<DormDTO> getAllDorms() {
        return dormRepository.findAll()
                .stream()
                .map(dormMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DormDTO getDormById(Long id) {
        Dorm dorm = dormRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dorm not found"));
        return dormMapper.toDTO(dorm);
    }

    @Override
    public DormDTO createDorm(DormDTO dormDTO) {
        Dorm dorm = dormMapper.toEntity(dormDTO);
        Dorm saved = dormRepository.save(dorm);
        return dormMapper.toDTO(saved);
    }

    @Override
    public DormDTO updateDorm(Long id, DormDTO updatedDormDTO) {
        Dorm existing = dormRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dorm not found"));

        // Мапваме DTO към entity и запазваме ID-то
        Dorm updatedEntity = dormMapper.toEntity(updatedDormDTO);
        updatedEntity.setId(existing.getId());

        Dorm saved = dormRepository.save(updatedEntity);
        return dormMapper.toDTO(saved);
    }

    @Override
    public void deleteDorm(Long id) {
        Dorm dorm = dormRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dorm not found"));
        dormRepository.delete(dorm);
    }

    @Override
    public DormDTO getDormByName(String name) {
        Dorm dorm = dormRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dorm not found"));
        return dormMapper.toDTO(dorm);
    }

    @Override
    public List<DormDTO> getDormsByPriceMax(Double maxPrice) {
        return dormRepository.findByPriceLessThanEqual(maxPrice)
                .stream()
                .map(dormMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DormDTO> getDormsByBlock(String blockNumber) {
        return dormRepository.findByBlockNumber(blockNumber)
                .stream()
                .map(dormMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DormDTO> searchDorms(String keyword) {
        return dormRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(dormMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DormDTO> getDormsByPriceRange(Double minPrice, Double maxPrice) {
        return dormRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(dormMapper::toDTO)
                .collect(Collectors.toList());
    }
}
