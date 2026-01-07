package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Dorm.DormRequestDTO;
import com.tuconnect.dorm_connect.dto.Dorm.DormResponseDTO;
import com.tuconnect.dorm_connect.dto.Dorm.DormUpdateRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DormService {

    List<DormResponseDTO> getAllDorms();

    DormResponseDTO getDormById(Long id);

    DormResponseDTO createDorm(DormRequestDTO dto, Authentication authentication, List<MultipartFile> files) throws IOException;

    DormResponseDTO updateDorm(
            Long dormId,
            DormUpdateRequestDTO dto,
            Authentication authentication,
            List<MultipartFile> files
    ) throws IOException;

    void deleteDorm(Long id);


}
