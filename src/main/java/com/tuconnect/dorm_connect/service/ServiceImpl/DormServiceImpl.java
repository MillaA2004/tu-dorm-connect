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
                .toList();
    }

    @Override
    public DormDTO getDormById(Long id) {
        Dorm dorm = dormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dorm not found with ID: " + id));

        return dormMapper.toDTO(dorm);
    }

    @Override
    public DormDTO createDorm(DormDTO dto) {

        Dorm dorm = new Dorm();
        dorm.setName(dto.name());
        dorm.setAddress(dto.address());
        dorm.setBlockNumber(dto.blockNumber());
        dorm.setAmenitiesJson(dto.amenitiesJson());
        dorm.setPrice(dto.price());

        Dorm saved = dormRepository.save(dorm);
        return dormMapper.toDTO(saved);
    }

    @Override
    public DormDTO updateDorm(Long id, DormDTO updatedDTO) {

        Dorm existingDorm = dormRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dorm not found with ID: " + id));

        existingDorm.setName(updatedDTO.name());
        existingDorm.setAddress(updatedDTO.address());
        existingDorm.setBlockNumber(updatedDTO.blockNumber());
        existingDorm.setAmenitiesJson(updatedDTO.amenitiesJson());
        existingDorm.setPrice(updatedDTO.price());

        Dorm saved = dormRepository.save(existingDorm);
        return dormMapper.toDTO(saved);
    }

    @Override
    public void deleteDorm(Long id) {
        Dorm existingDorm = dormRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dorm not found with ID: " + id));

        dormRepository.delete(existingDorm);
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
                .toList();
    }

    @Override
    public List<DormDTO> getDormsByBlock(String blockNumber) {
        return dormRepository.findByBlockNumber(blockNumber)
                .stream()
                .map(dormMapper::toDTO)
                .toList();
    }

    @Override
    public List<DormDTO> searchDorms(String keyword) {
        return dormRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(dormMapper::toDTO)
                .toList();
    }

    @Override
    public List<DormDTO> getDormsByPriceRange(Double minPrice, Double maxPrice) {
        return dormRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(dormMapper::toDTO)
                .toList();
    }
}
