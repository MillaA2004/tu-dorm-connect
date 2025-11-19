package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.DormDTO;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.repository.DormRepository;
import com.tuconnect.dorm_connect.service.DormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DormServiceImpl implements DormService {

    private final DormRepository dormRepository;

    private DormDTO toDTO(Dorm dorm) {
        DormDTO dto = new DormDTO();
        dto.setId(dorm.getId());
        dto.setName(dorm.getName());
        dto.setAddress(dorm.getAddress());
        dto.setBlockNumber(dorm.getBlockNumber());
        dto.setAmenitiesJson(dorm.getAmenitiesJson());
        dto.setPrice(dorm.getPrice());
        return dto;
    }

    private Dorm toEntity(DormDTO dto) {
        Dorm dorm = new Dorm();
        dorm.setId(dto.getId());
        dorm.setName(dto.getName());
        dorm.setAddress(dto.getAddress());
        dorm.setBlockNumber(dto.getBlockNumber());
        dorm.setAmenitiesJson(dto.getAmenitiesJson());
        dorm.setPrice(dto.getPrice());
        return dorm;
    }

    @Override
    public List<DormDTO> getAllDorms() {
        return dormRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DormDTO getDormById(Long id) {
        Dorm dorm = dormRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dorm not found"));
        return toDTO(dorm);
    }

    @Override
    public DormDTO createDorm(DormDTO dormDTO) {
        Dorm dorm = toEntity(dormDTO);
        Dorm saved = dormRepository.save(dorm);
        return toDTO(saved);
    }

    @Override
    public DormDTO updateDorm(Long id, DormDTO updatedDormDTO) {
        Dorm existing = dormRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dorm not found"));

        existing.setName(updatedDormDTO.getName());
        existing.setAddress(updatedDormDTO.getAddress());
        existing.setBlockNumber(updatedDormDTO.getBlockNumber());
        existing.setAmenitiesJson(updatedDormDTO.getAmenitiesJson());
        existing.setPrice(updatedDormDTO.getPrice());

        Dorm saved = dormRepository.save(existing);
        return toDTO(saved);
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
        return toDTO(dorm);
    }

    @Override
    public List<DormDTO> getDormsByPriceMax(Double maxPrice) {
        return dormRepository.findByPriceLessThanEqual(maxPrice)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DormDTO> getDormsByBlock(String blockNumber) {
        return dormRepository.findByBlockNumber(blockNumber)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DormDTO> searchDorms(String keyword) {
        return dormRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DormDTO> getDormsByPriceRange(Double minPrice, Double maxPrice) {
        return dormRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
