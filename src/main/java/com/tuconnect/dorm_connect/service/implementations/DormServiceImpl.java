package com.tuconnect.dorm_connect.service.implementations;


import com.tuconnect.dorm_connect.dto.Dorm.DormRequestDTO;
import com.tuconnect.dorm_connect.dto.Dorm.DormResponseDTO;
import com.tuconnect.dorm_connect.dto.Dorm.DormUpdateRequestDTO;
import com.tuconnect.dorm_connect.mapper.DormMapper;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.repository.DormRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.CloudinaryService;
import com.tuconnect.dorm_connect.service.DormService;

import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Service
public class DormServiceImpl implements DormService {

    private final DormRepository dormRepository;
    private final DormMapper dormMapper;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    
    public DormServiceImpl(DormRepository dormRepository, DormMapper dormMapper, UserRepository userRepository,CloudinaryService cloudinaryService) {
        this.dormRepository = dormRepository;
        this.dormMapper = dormMapper;
        this.userRepository=userRepository;
        this.cloudinaryService=cloudinaryService;
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
    public void deleteDorm(Long id) {
        Dorm dorm = dormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dorm not found"));
        dormRepository.delete(dorm);
    }



    @Override
    @Transactional
    public DormResponseDTO createDorm(DormRequestDTO dto, Authentication authentication, List<MultipartFile> files) throws IOException {

        String email = authentication.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        if (user.getRole() != Roles.Admin) {
            throw new IllegalArgumentException("Only Admins are allowed to create!");
        }

        List<String> imageUrls = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String url = cloudinaryService.uploadFile(file);
                    imageUrls.add(url);
                }
            }
        }

        Dorm dorm = new Dorm();
        dorm.setName(dto.name());
        dorm.setAddress(dto.address());
        dorm.setDescription(dto.description());
        dorm.setPrice(dto.price());
        dorm.setLatitude(dto.latitude());
        dorm.setLongitude(dto.longitude());
        dorm.setImageUrlsList(imageUrls);

        Dorm saved = dormRepository.save(dorm);
        return dormMapper.toDTO(saved);
    }


    @Override
    @Transactional
    public DormResponseDTO updateDorm(
            Long dormId,
            DormUpdateRequestDTO dto,
            Authentication authentication,
            List<MultipartFile> files
    ) throws IOException {

        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Roles.Admin) {
            throw new IllegalArgumentException("Only Admins are allowed to update!");
        }

        Dorm dorm = dormRepository.findById(dormId)
                .orElseThrow(() -> new IllegalArgumentException("Dorm not found"));


        dorm.setName(dto.name());
        dorm.setAddress(dto.address());
        dorm.setDescription(dto.description());
        dorm.setPrice(dto.price());
        dorm.setLatitude(dto.latitude());
        dorm.setLongitude(dto.longitude());


        if (dorm.getImageUrlsList() == null) {
            dorm.setImageUrlsList(new ArrayList<>());
        }

        boolean hasNewFiles = files != null && files.stream().anyMatch(f -> f != null && !f.isEmpty());
        boolean replace = Boolean.TRUE.equals(dto.replaceImages());

        if (hasNewFiles) {
            if (replace) {

                dorm.getImageUrlsList().clear();
            }

            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String url = cloudinaryService.uploadFile(file);
                    dorm.getImageUrlsList().add(url);
                }
            }
        }

        return dormMapper.toDTO(dormRepository.save(dorm));
    }







}
